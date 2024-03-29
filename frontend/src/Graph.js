import React from 'react';
import {save} from 'save-file';
import Files from 'react-files';
import axios from "axios";
import {GraphView} from 'react-digraph';
import './Graph.css';
import {makeGraphConfigObject, NODE_KEY, SINK_TYPE, SOURCE_TYPE, STANDARD_EDGE_TYPE} from './Graph.configs';
import {
    Col, Container, Row,
    Button,
    Dropdown, DropdownButton,
    Card,
    Form,
} from 'react-bootstrap';
import toposort from 'toposort';
import LoadingScreen from 'react-loading-screen';
import { withAlert } from "react-alert";
// import {sample as GRAPH_SAMPLE} from "./Graph.sample";


class Graph extends React.Component {

    constructor(props) {
        super(props);

        let sample = {nodes: [], edges: []};
        // let sample = GRAPH_SAMPLE;

        this.state = {
            graph: sample,
            selected: {},
            nodeCounter: sample.nodes.length,
            graphConfig: null,
            selectedType: "",
            selectedSubType: "",
            edgeSource: "",
            edgeTarget: "",
            panNode: "",
            graphId: Math.random().toString(36).substr(2, 5),
        };

        this.GraphView = React.createRef();
    }

    async componentDidMount() {
        let config = await makeGraphConfigObject();
        console.log('** GRAPH CONFIG **');
        console.log(config);
        this.setState({graphConfig: config});
    }

    isLoading() {
        return this.state.graphConfig === null;
    }

    /*
     * Handlers/Interaction
     */

    // Called by 'drag' handler, etc.. to sync updates from D3 with the graph
    onUpdateNode(viewNode) {
        const graph = this.state.graph;
        const i = this.getNodeIndex(viewNode);

        graph.nodes[i] = viewNode;
        this.setState({graph});
    }

    // Node 'mouseUp' handler
    onSelectNode(viewNode) {
        // Deselect events will send Null viewNode
        this.setState({selected: viewNode});
    }

    // Edge 'mouseUp' handler
    onSelectEdge(viewEdge) {
        this.setState({selected: viewEdge});
    }

    // Updates the graph with a new node
    onCreateNode(event) {
        if (isObjectEmpty(this.state.selectedType) || isObjectEmpty(this.state.selectedSubType)) {
            this.props.alert.error('Please select an option from "Add Node"');
            return;
        }

        const graph = this.state.graph;

        let id = this.state.nodeCounter + 1;
        const nodeType = this.state.selectedType;
        let nodeSubType = this.state.selectedSubType;

        let settingsOptions = this.state.graphConfig.NodeSubtypes[nodeSubType].settings || [];
        let settings = Object.assign({}, ...(settingsOptions.map(el => {
            let tmp = {};
            tmp[el] = "";
            return tmp;
        })));

        let nodeTitle = nodeSubType + '-' + id
        const viewNode = {
            id: this.state.graphId + '-' + this.state.selectedType.substring(0, 2) + id,
            title: nodeTitle,
            type: nodeType,
            subtype: nodeSubType,
            x: 10,
            y: 0,
            settings: settings
        };

        console.log('** NEW NODE **');
        console.log(viewNode);

        graph.nodes = [
            ...graph.nodes,
            viewNode
        ];
        this.setState({
            graph: graph,
            nodeCounter: this.state.nodeCounter + 1
        });

        this.props.alert.success('Successfully created new node');
    }

    // Deletes a node from the graph
    onDeleteNode(viewNode, nodeId, nodeAdr) {
        const graph = this.state.graph;
        // Delete any connected edges

        const newEdges = graph
            .edges
            .filter(edge => {
                return edge.source !== nodeId && edge.target !== nodeId;
            });

        graph.nodes = nodeAdr;
        graph.edges = newEdges;

        this.setState({graph, selected: null});
        // this.alert.show(`Successfully deleted node ${nodeId}`); // TODO
    }

    // Creates a new node between two edges
    async onCreateEdge(event) {
      console.log(event.target);
      if (this.state.edgeSource === "" || this.state.edgeTarget === "") {
          this.props.alert.error('Please select Source and Target nodes');
          return;
      }

      let sourceNode = this.state.edgeSource;
      let sinkNode = this.state.edgeTarget;

      let sourceViewNode = this.getViewNode(sourceNode);
      let sinkViewNode = this.getViewNode(sinkNode);

      if(sourceViewNode.subtype === "RECIPE"){
        sourceViewNode = sourceViewNode.output;
      }
      if(sinkViewNode.subtype === "RECIPE"){
        sinkViewNode = sinkViewNode.input;
      }

      console.log(sinkNode);
      console.log(sinkViewNode);

      const type = STANDARD_EDGE_TYPE;

      const graph = this.state.graph;
      const viewEdge = {
          source: sourceNode,
          target: sinkNode,
          type
      };

      // Check if Edge is duplicated
      let isDuplicated = false;
      this.state.graph.edges.forEach(el => {
          if (el.source === sourceNode && el.target === sinkNode) {
              isDuplicated = true;
          }
      });

      // Check if Edge is valid
      if (isDuplicated) {
          this.props.alert.error('Edge already exists');
      } else if (sourceNode === sinkNode) {
          this.props.alert.error(`Trying to create an edge from node ${sourceViewNode.title} to itself`);
      } else if (sourceViewNode.type === SINK_TYPE) {
          this.props.alert.error(`Trying to create an output edge from the sink node ${sourceViewNode.title}`);
      } else if (sinkViewNode.type === SOURCE_TYPE) {
          this.props.alert.error(`Trying to create an input edge to the source node ${sinkViewNode.title}`);
      } else if (!isGraphAcyclic(this.state.graph)) {
          this.props.alert.error('Edge creation would create a cycle in the graph');
      } else if (!await Graph.isValidEdge(sourceViewNode, sinkViewNode).catch(_ => false)) {
          this.props.alert.error('Trying to create invalid edge type between selected source and sink');
      } else { // Else, create the edge (it's valid)
          graph.edges = [
              ...graph.edges,
              viewEdge
          ];

          this.setState({graph, selected: viewEdge});
          this.props.alert
              .success('Successfully created a new edge');
      }
    }

    static async isValidEdge(sourceViewNode, sinkViewNode) {

        const source = {
            subtype: sourceViewNode.subtype,
            type: sourceViewNode.type
        };

        const sink = {
            subtype: sinkViewNode.subtype,
            type: sinkViewNode.type
        };

        const edge = {
            output: source,
            input: sink
        };

        let {data} = await axios.post(process.env.REACT_APP_API_URL + '/checkEdge', JSON.stringify(edge));
        return data;
    }

    // Called when an edge is reattached to a different target.
    onSwapEdge(sourceViewNode, targetViewNode, viewEdge) {
        const graph = this.state.graph;
        const i = this.getEdgeIndex(viewEdge);
        const edge = JSON.parse(JSON.stringify(graph.edges[i]));

        edge.source = sourceViewNode[NODE_KEY];
        edge.target = targetViewNode[NODE_KEY];
        graph.edges[i] = edge;
        // reassign the array reference if you want the graph to re-render a swapped
        // edge
        graph.edges = [...graph.edges];

        this.setState({graph, selected: edge});
    }

    // Called when an edge is deleted
    onDeleteEdge(viewEdge, edges) {
        const graph = this.state.graph;
        graph.edges = edges;
        this.setState({graph, selected: null});
    }

    // Helper to find the index of a given node
    getNodeIndex(searchNode) {
        return this.state.graph.nodes
            .findIndex((node) => {
                return node[NODE_KEY] === searchNode[NODE_KEY];
            });
    }

    // Helper to find the index of a given edge
    getEdgeIndex(searchEdge) {
        return this.state.graph.edges
            .findIndex((edge) => {
                return edge.source === searchEdge.source && edge.target === searchEdge.target;
            });
    }

    // Given a nodeKey, return the corresponding node
    getViewNode(nodeKey) {
        const searchNode = {};
        searchNode[NODE_KEY] = nodeKey;
        const i = this.getNodeIndex(searchNode);
        return this.state.graph.nodes[i];
    }

    handleChange(event) {
        console.log('Handling Change onBlur');
        console.log(event);

        this.setState({
            nodeCounter: parseInt(event.target.value || '0', 10)
        },);
    }

    onSelectPanNode(eventKey, event) {
        if (this.GraphView) {
            this.GraphView.panToNode(eventKey, true);
            this.setState({panNode: this.getViewNode(eventKey).title})
        }
    }

    isGraphValid(graph) {
        // Check if it's a DAG
        if (! isGraphAcyclic(graph)) return false;

        // Check if settings are filled in
        return graph.nodes
            .map(n => {
                if (n.settings == null || isObjectEmpty(n.settings))
                    return true;

                return Object.getOwnPropertyNames(n.settings)
                    .map(propName =>
                        n.settings[propName] != null && n.settings[propName].length > 0
                    ).reduce((el1, el2) => el1 && el2);
            })
            .reduce((el1, el2) => el1 && el2); // Check if condition holds for all nodes
    }

    onRunGraph() {
        if (! this.isGraphValid(this.state.graph)) {
            this.props.alert.error('Graph is invalid.');
            return;
        } else {
            this.props.alert.success('Graph is valid! Running...');
        }

        let graph = JSON.parse(JSON.stringify(this.state.graph));
        let recipes = this.state.graph.nodes.filter(n => n.subtype === "RECIPE");
        recipes.forEach(recipe => {
          const recipeGraph = JSON.parse(recipe.graph);
          let nodes = recipeGraph.nodes;
          let edges = recipeGraph.edges;
          nodes.forEach(node => {
            node.id = node.id + recipe.title;
          });
          edges.forEach(edge => {
            edge.source =  edge.source+ recipe.title;
            edge.target =  edge.target+ recipe.title;
          });

          let graphEdges = graph.edges.filter(edge => edge.target !== recipe.id && edge.source !== recipe.id);

          let outputEdges = graph.edges.filter(edge => edge.source === recipe.id);
          outputEdges.forEach(edge => {
            edge.source =  recipe.output.id;
          });

          let inputEdges = graph.edges.filter(edge => edge.target === recipe.id);
          inputEdges.forEach(edge => {
            edge.target =  recipe.input.id;
          });

          graph.edges = [
            ...graphEdges,
            ...edges,
            ...inputEdges,
            ...outputEdges
          ];
          graph.nodes = [
            ...graph.nodes,
            ...nodes
          ];
        });

        console.log(graph);
        graph.nodes = graph.nodes.filter(node => node.subtype !== "RECIPE");

        axios
            .post(process.env.REACT_APP_API_URL + '/sendGraph', JSON.stringify(graph))
            .then(response => {
                console.log(response);
                if (response.data === true) {
                    this.sendRunRequest();
                } else {
                    console.warn('There was an error parsing the graph.');
                }
            })
            .catch(error => console.warn('Error on axios.post: ' + JSON.stringify(error)));
    }

    sendRunRequest() {
        axios
            .get(process.env.REACT_APP_API_URL + '/runGraph', JSON.stringify({}))
            .then(response => console.log(response))
            .catch(error => console.warn('Error on axios.get: ' + JSON.stringify(error)));
    }

    sendStopRequest() {
        axios
            .get(process.env.REACT_APP_API_URL + '/stopGraph', JSON.stringify({}))
            .then(response => console.log(response))
            .catch(error => console.warn('Error on axios.get: ' + JSON.stringify(error)));
    }

    getNodeTypes() {
        if (this.state.graphConfig === null) 
            return {};
        let graphConfig = this.state.graphConfig;
        let nodeTypes = new Map();
        for (let prop in graphConfig.NodeTypes) {
            let parentTypeId = graphConfig.NodeTypes[prop].shapeId;
            let parentType = parentTypeId.split('#')[1];
            nodeTypes[parentType] = [];
        }

        for (let subType in graphConfig.NodeSubtypes) {
            let parentType = graphConfig.NodeSubtypes[subType].nodeType;
            nodeTypes[parentType].push(subType);
        }

        return nodeTypes;
    }

    handleTypeSelectorChange(eventKey, event) {
        this.setState({selectedType: eventKey});
        console.log(`Changing opt1 to "${eventKey}"`);
    }

    handleSubTypeSelectorChange(eventKey, event) {
        this.setState({selectedSubType: eventKey});
        console.log(`Changing opt2 to "${eventKey}"`);
    }

    handleEdgeSourceChange(eventKey, event) {
        this.setState({edgeSource: eventKey});
        console.log(`Changing edge source to "${eventKey}"`);
    }

    handleEdgeTargetChange(eventKey, event) {
        this.setState({edgeTarget: eventKey});
        console.log(`Changing edge target to "${eventKey}"`);
    }

    onFilesChange(files) {
        let reader = new FileReader();
        reader.onload = () => {
            var data = reader.result;
            if (this.loadGraph(data)) {
                this.props.alert
                    .info('Loaded graph successfully!');
            } else {
                this.props.alert
                    .error('The graph couldn`t be loaded. Please check if the submitted graph is in the righ' +
                            't format!');
            }
        };
        reader.readAsText(files[0]);
    }

    saveGraph() {
        save(JSON.stringify(this.state.graph), 'graph.json');
    }

    loadGraph(graphObj) {

        if (graphObj == null) 
            return false;
        
        let jsonGraph = JSON.parse(graphObj);

        let nodes = jsonGraph.nodes;
        let edges = jsonGraph.edges;

        if (nodes == null || edges == null) 
            return false;
        
        const graph = this.state.graph;

        graph.nodes = nodes;
        graph.edges = edges;

        this.setState({graph, selected: null, nodeCounter: nodes.length});

        return true;
    }

    deleteGraph() {
        let sample = { nodes: [], edges: [] };
        let cleanState = {
            graph: sample,
            selected: {},
            nodeCounter: sample.nodes.length,
            selectedType: "",
            selectedSubType: "",
            edgeSource: "",
            edgeTarget: "",
            panNode: "",
            graphId: Math.random().toString(36).substr(2, 5),
        };
        this.setState(cleanState);
    }

    static onFilesError(error, file) {
        console.log('error code ' + error.code + ': ' + error.message)
    }

    saveRecipe() {
      let handlers = this.state.graph.nodes.filter(n => n.type === "handlerNode");
      if(handlers.length !== this.state.graph.nodes.length){
        this.props.alert
                    .error('Recipes must be only composed by handler nodes!');
        return;
      }
      let sources = this.state.graph.nodes.filter(n => this.state.graph.edges.filter(e => e.target === n.id).length === 0);
      if(sources.length>1){
        this.props.alert
                    .error('You can`t export a recipe with more than 1 handler input node!');
        return;
      }
      console.log(sources);
      let sinks = this.state.graph.nodes.filter(n => this.state.graph.edges.filter(e => e.source === n.id).length === 0);
      if(sinks.length>1){
        this.props.alert
                    .error('You can`t export a recipe with more than 1 handler output node!');
        return;
      }
      save(JSON.stringify(this.state.graph), 'recipe.json');
    }

    onNewRecipe(files) {
      let reader = new FileReader();
      reader.onload = () => {
          var data = reader.result;
          if (this.loadRecipe(data)) {
              this.props.alert
                  .info('Imported recipe successfully!');
          } else {
              this.props.alert
                  .error('The recipe couldn`t be loaded. Please check if the submitted recipe is in the righ' +
                          't format!');
          }
      };
      reader.readAsText(files[0]);
    }

    loadRecipe(graphObj) {

      if (graphObj == null) 
          return false;
      
      let jsonGraph = JSON.parse(graphObj);

      let nodes = jsonGraph.nodes;
      let edges = jsonGraph.edges;

      if (nodes == null || edges == null) 
          return false;
      
      const graph = this.state.graph;

      let id = this.state.nodeCounter + 1;
      const nodeType = "handlerNode";
      let nodeSubType = "RECIPE";

      let nodeTitle = nodeSubType + '-' + id
      let input = jsonGraph.nodes.filter(n => jsonGraph.edges.filter(e => e.target === n.id).length === 0)[0];
      let output = jsonGraph.nodes.filter(n => jsonGraph.edges.filter(e => e.source === n.id).length === 0)[0];

      input.id = input.id + nodeTitle;
      output.id = output.id + nodeTitle;

      const viewNode = {
        id: "ndlerNode" + id,
        title: nodeTitle,
        type: nodeType,
        subtype: nodeSubType,
        graph: graphObj,
        input: input,
        output: output,
        x: 10,
        y: 0
      };

      graph.nodes = [
        ...graph.nodes,
        viewNode
      ];
      this.setState({
          graph: graph,
          nodeCounter: this.state.nodeCounter + 1
      });

      console.log(this.state.graph);

      return true;
    }

    renderLoadingScreen() {
        return (
            <LoadingScreen loading={this.isLoading()}>
                LOADING
            </LoadingScreen>
        );
    }

    sequenceToOptions(seq) {
        return seq.map(el => Object.assign({}, {
            value: el,
            label: el
        }));
    }

    selectOptions() {
        let nodeTypes = this.getNodeTypes();
        let firstOptions = Object.keys(nodeTypes);
        firstOptions = this.sequenceToOptions(firstOptions);

        let selectedType = this.state.selectedType;
        if (isObjectEmpty(selectedType)) {
            selectedType = firstOptions[0].value;
        }

        let secondOptions = nodeTypes[selectedType];
        secondOptions = this.sequenceToOptions(secondOptions);

        return {firstOptions, secondOptions};
    }

    handleNodeSettingsChange(event) {
        let settingProp = event.target['placeholder'];
        let settingValue = event.target.value;

        let selected = this.state.selected;
        selected.settings[settingProp] = settingValue;

        this.setState({selected});
    }

    renderNodeSettings() {
        let defaultReturn = (<div>(no node selected...)</div>);

        if (this.state.selected === null || isObjectEmpty(this.state.selected)) {
            return defaultReturn;
        }
        if (! this.state.selected.hasOwnProperty('settings')) return defaultReturn;

        return (
            Object.getOwnPropertyNames(this.state.selected.settings).map(el =>
                <Form.Group controlId={`form-for-${el}`} key={el}>
                    <Row>
                        <Col><Form.Label>{el}</Form.Label></Col>
                        <Col>
                            <Form.Control type="text" placeholder={el}
                                value={this.state.selected.settings[el]}
                                onChange={this.handleNodeSettingsChange.bind(this)}/>
                        </Col>
                    </Row>
                </Form.Group>
            )
        );
    }

    renderNodeInputOutput() {
        let defaultReturn = (<div>(no node selected...)</div>);
        
        if (this.state.selected === null || isObjectEmpty(this.state.selected)) {
            return defaultReturn;
        }

        let nodeType = this.state.graphConfig.NodeSubtypes[this.state.selected.subtype];
        if (!nodeType) return defaultReturn;

        let inputType = nodeType.inputType;
        let outputType = nodeType.outputType;

        let inputJSX = (<Col></Col>);
        if (inputType !== null && inputType.length > 0) {
            inputJSX = (<Col>Input: "{inputType}"</Col>);
        }

        let outputJSX = (<Col></Col>);
        if (outputType !== null && outputType.length > 0) {
            outputJSX = (<Col>Output: "{outputType}"</Col>);
        }

        return (
            <Row>
                {inputJSX}
                {outputJSX}
            </Row>
        );
    }

    render() {
        if (this.isLoading()) 
            return this.renderLoadingScreen();
        
        const nodes = this.state.graph.nodes;
        const edges = this.state.graph.edges;
        const selected = this.state.selected;

        const NodeTypes = this.state.graphConfig.NodeTypes;
        const NodeSubtypes = this.state.graphConfig.NodeSubtypes;
        const EdgeTypes = this.state.graphConfig.EdgeTypes;

        const {firstOptions, secondOptions} = this.selectOptions();
        let edgeNodeSource = this.getViewNode(this.state.edgeSource);
        let edgeNodeTarget = this.getViewNode(this.state.edgeTarget);

        return (
            <Container id='graph'>
                <Row>
                    <Col id='graph-view' sm={8}>
                        <GraphView
                            ref={(el) => (this.GraphView = el)}
                            nodeKey={NODE_KEY}
                            nodes={nodes}
                            edges={edges}
                            selected={selected}
                            nodeTypes={NodeTypes}
                            nodeSubtypes={NodeSubtypes}
                            edgeTypes={EdgeTypes}
                            onSelectNode={this.onSelectNode.bind(this)}
                            onCreateNode={this.onCreateNode.bind(this)}
                            onUpdateNode={this.onUpdateNode.bind(this)}
                            onDeleteNode={this.onDeleteNode.bind(this)}
                            onSelectEdge={this.onSelectEdge.bind(this)}
                            onSwapEdge={this.onSwapEdge.bind(this)}
                            onDeleteEdge={this.onDeleteEdge.bind(this)}/>
                    </Col>

                    <Col id='graph-settings' sm={4}>
                        <h1>Control Panel</h1>

                        <Card className="create-node">
                            <Card.Body>
                                <Card.Title>Add Node</Card.Title>

                                <DropdownButton id='firstOption' title={this.state.selectedType || 'Node Type'} variant='outline-primary'>
                                    {firstOptions.map((node, idx) => <Dropdown.Item
                                        key={idx}
                                        onSelect={this.handleTypeSelectorChange.bind(this)}
                                        eventKey={node.value}>{node.value}</Dropdown.Item>)}
                                </DropdownButton>

                                <DropdownButton id='secondOption' title={this.state.selectedSubType || 'Sub-Node Type'} variant='outline-primary'>
                                    {secondOptions.map((node, idx) => <Dropdown.Item
                                        key={idx}
                                        onSelect={this.handleSubTypeSelectorChange.bind(this)}
                                        eventKey={node.value}>{node.value}</Dropdown.Item>)}
                                </DropdownButton>

                                <Button variant='outline-success' onClick={this.onCreateNode.bind(this)}>Create</Button>
                            </Card.Body>
                        </Card>


                        <Card className="create-edge">
                            <Card.Body>
                                <Card.Title>Add Edge</Card.Title>
                                <DropdownButton id='source' title={(edgeNodeSource ? edgeNodeSource.title : null) || 'Edge Source'} variant='outline-primary'
                                    onSelect={this.handleEdgeSourceChange.bind(this)}>
                                    {nodes.map((node) => <Dropdown.Item key={node[NODE_KEY]} eventKey={node[NODE_KEY]}>{node.title}</Dropdown.Item>)}
                                </DropdownButton>

                                <DropdownButton id='sink' title={(edgeNodeTarget ? edgeNodeTarget.title : null) || 'Edge Target'} variant='outline-primary'
                                    onSelect={this.handleEdgeTargetChange.bind(this)}>
                                    {nodes.map((node) => <Dropdown.Item key={node[NODE_KEY]} eventKey={node[NODE_KEY]}>{node.title}</Dropdown.Item>)}
                                </DropdownButton>

                                <Button variant='outline-success'
                                    onClick={this.onCreateEdge.bind(this)}>Create</Button>
                            </Card.Body>
                        </Card>

                        <Card className='pan-to-node'>
                            <Card.Body>
                                <Card.Title>Pan to Node</Card.Title>
                                <DropdownButton id='panToSelection' title={this.state.panNode || 'Select Node'} variant='outline-primary'
                                    onSelect={this.onSelectPanNode.bind(this)}>
                                    {nodes.map((node) => <Dropdown.Item key={node[NODE_KEY]} eventKey={node[NODE_KEY]}>{node.title}</Dropdown.Item>)}
                                </DropdownButton>
                            </Card.Body>
                        </Card>

                        <Card className='graph-run-settings'>
                            <Card.Body>
                                <Card.Title>Graph</Card.Title>
                                <div className="send-backend-run">
                                    <Button variant='outline-success'
                                        onClick={this
                                        .onRunGraph
                                        .bind(this)}>Run</Button>
                                </div>
                                <div className="send-backend-stop">
                                    <Button variant='outline-danger'
                                        onClick={this
                                        .sendStopRequest
                                        .bind(this)}>Stop</Button>
                                </div>
                                <div id='graph-file-settings'>
                                    <Files
                                        onChange={this
                                        .onFilesChange
                                        .bind(this)}
                                        onError={Graph.onFilesError}
                                        accepts={['.json']}
                                        maxFiles={1}
                                        maxFileSize={10000000}
                                        minFileSize={0}
                                        clickable>
                                        <Button variant='outline-primary'>Upload</Button>
                                    </Files>
                                </div>
                                <div>
                                    <Button variant='outline-primary'
                                        onClick={this
                                        .saveGraph
                                        .bind(this)}>Save</Button>
                                </div>
                                <div>
                                    <Button variant='danger'
                                        onClick={this
                                        .deleteGraph
                                        .bind(this)}>Delete</Button>
                                </div>
                            </Card.Body>                        
                        </Card>

                        <Card className='recipe-settings'>
                            <Card.Body>
                                <Card.Title>Recipe</Card.Title>
                                <div id='recipe-file-settings'>
                                    <Files
                                        onChange={this
                                        .onNewRecipe
                                        .bind(this)}
                                        onError={Graph.onFilesError}
                                        accepts={['.json']}
                                        maxFiles={1}
                                        maxFileSize={10000000}
                                        minFileSize={0}
                                        clickable>
                                        <Button variant='outline-primary'>Import</Button>
                                    </Files>
                                </div>
                                <div>
                                    <Button variant='outline-primary'
                                        onClick={this
                                        .saveRecipe
                                        .bind(this)}>Export</Button>
                                </div>
                            </Card.Body>                        
                        </Card>

                        <Card>
                            <Card.Body>
                                <Card.Title>Node Settings</Card.Title>
                                <div id='node-settings'>
                                {this.renderNodeSettings()}
                                </div>
                            </Card.Body>
                        </Card>

                        <Card>
                            <Card.Body>
                                <Card.Title>Node Input &amp; Output</Card.Title>
                                <div id='node-input-output'>
                                {this.renderNodeInputOutput()}
                                </div>
                            </Card.Body>
                        </Card>

                    </Col>
                </Row>
            </Container>
        );
    }

}

function isGraphAcyclic(graph) {
    let directedEdges = graph.edges.map(el => [el.source, el.target]);

    try { // Try to obtain topological sort
        toposort(directedEdges);
    } catch (err) {
        return false;
    }

    return true;
}

function isObjectEmpty(obj) {
    return Object
        .keys(obj)
        .length === 0;
}

export default withAlert()(Graph)
