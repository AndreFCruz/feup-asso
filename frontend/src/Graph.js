import React from 'react';
import {save} from 'save-file';
import Files from 'react-files';
import axios from "axios";
import {GraphView} from 'react-digraph';
import './Graph.css';
import {sample as GRAPH_SAMPLE} from "./Graph.sample";
import {makeGraphConfigObject, NODE_KEY, SINK_TYPE, SOURCE_TYPE, STANDARD_EDGE_TYPE} from './Graph.configs';
import {
    Col, Container, Row,
    Button, ButtonToolbar,
    Dropdown, DropdownButton,
} from 'react-bootstrap';
import toposort from 'toposort';
import LoadingScreen from 'react-loading-screen';
import { withAlert } from "react-alert";


class Graph extends React.Component {

    constructor(props) {
        super(props);

        // let sample = {     nodes: [], edges: [] };
        let sample = GRAPH_SAMPLE;

        this.state = {
            graph: sample,
            selected: {},
            nodeCounter: sample.nodes.length,
            graphConfig: null,
            selectedType: "",
            selectedSubType: "",
            edgeSource: "",
            edgeTarget: "",
        };

        this.GraphView = React.createRef();
    }

    async componentDidMount() {
        let config = await makeGraphConfigObject();
        console.log('** GRAPH CONFIG **');
        console.log(config);
        this.setState({graphConfig: config});
        this.setSelectedTypeAndSubType();
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
            id: this.state.selectedType.substring(0, 2) + id,
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
        console.log(event.target); debugger;
        if (this.state.edgeSource === "" || this.state.edgeTarget === "") {
            this.props.alert.error('Please select Source and Target nodes');
            return;
        }

        let sourceNode = this.state.edgeSource;
        let sinkNode = this.state.edgeTarget;
        const type = STANDARD_EDGE_TYPE;

        const graph = this.state.graph;
        const viewEdge = {
            source: sourceNode,
            target: sinkNode,
            type
        };

        let sourceViewNode = this.getViewNode(sourceNode);
        let sinkViewNode = this.getViewNode(sinkNode);

        // Check if Edge is duplicated
        let isDuplicated = false;
        this.state.graph.edges.forEach(el => {
            if (el.source === sourceNode && el.target === sinkNode) {
                isDuplicated = true;
            }
        });

        // Check if Edge is valid
        if (isDuplicated) {
            this.props.alert
                .error('Edge already exists');
        } else if (sourceNode === sinkNode) {
            this.props.alert
                .error(`Trying to create an edge from node ${sourceViewNode.title} to itself`);
        } else if (sourceViewNode.type === SINK_TYPE) {
            this.props.alert
                .error(`Trying to create an output edge from the sink node ${sourceViewNode.title}`);
        } else if (sinkViewNode.type === SOURCE_TYPE) {
            this.props.alert
                .error(`Trying to create an input edge to the source node ${sinkViewNode.title}`);
        } else if (!isGraphAcyclic(this.state.graph)) {
            this.props.alert
                .error('Edge creation would create a cycle in the graph');
        } else if (!await Graph.isValidEdge(sourceViewNode, sinkViewNode).catch(_ => false)) {
            this.props.alert
                .error('Trying to create invalid edge type between selected source and sink');
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
        }
    }

    onRunGraph() {
        axios
            .post(process.env.REACT_APP_API_URL + '/sendGraph', JSON.stringify(this.state.graph))
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

    static onFilesError(error, file) {
        console.log('error code ' + error.code + ': ' + error.message)
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

    setSelectedTypeAndSubType() {
        const {firstOptions, secondOptions} = this.selectOptions();
        this.setState({selectedType: firstOptions[0].value});
        this.setState({selectedSubType: secondOptions[0].value})
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

                        <div className="create-node">
                            <h5>Add Node</h5>

                            <DropdownButton id='firstOption' title='Node Type' variant='outline-primary'>
                                {firstOptions.map((node, idx) => <Dropdown.Item
                                    key={idx}
                                    onSelect={this.handleTypeSelectorChange.bind(this)}
                                    eventKey={node.value}>{node.value}</Dropdown.Item>)}
                            </DropdownButton>

                            <DropdownButton id='secondOption' title='Sub-Node Type' variant='outline-primary'>
                                {secondOptions.map((node, idx) => <Dropdown.Item
                                    key={idx}
                                    onSelect={this.handleSubTypeSelectorChange.bind(this)}
                                    eventKey={node.value}>{node.value}</Dropdown.Item>)}
                            </DropdownButton>

                            <Button variant='outline-primary' onClick={this.onCreateNode.bind(this)}>Create</Button>
                        </div>
                        <div className="create-edge">
                            <h5>Add Edge</h5>

                            <DropdownButton id='source' title='Edge Source' variant='outline-primary'
                                onSelect={this.handleEdgeSourceChange.bind(this)}>
                                {nodes.map((node) => <Dropdown.Item key={node[NODE_KEY]} eventKey={node[NODE_KEY]}>{node.title}</Dropdown.Item>)}
                            </DropdownButton>

                            <DropdownButton id='sink' title='Edge Target' variant='outline-primary'
                                onSelect={this.handleEdgeTargetChange.bind(this)}>
                                {nodes.map((node) => <Dropdown.Item key={node[NODE_KEY]} eventKey={node[NODE_KEY]}>{node.title}</Dropdown.Item>)}
                            </DropdownButton>

                            <Button variant='outline-primary'
                                onClick={this.onCreateEdge.bind(this)}>Create</Button>
                        </div>
                        <div>
                            <h5>Pan to Node</h5>
                            <DropdownButton id='panToSelection' title='Select Node' variant='outline-primary'
                                onSelect={this.onSelectPanNode.bind(this)}>
                                {nodes.map((node) => <Dropdown.Item key={node[NODE_KEY]} eventKey={node[NODE_KEY]}>{node.title}</Dropdown.Item>)}
                            </DropdownButton>

                        </div>

                        <div>
                            <div className="send-backend-run">
                                <Button variant='outline-primary'
                                    onClick={this
                                    .onRunGraph
                                    .bind(this)}>Run</Button>
                            </div>
                            <div id='graph-file-settings'>
                                <span>Load graph:</span>
                                <div className="files">
                                    <Files
                                        className='files-dropzone'
                                        onChange={this
                                        .onFilesChange
                                        .bind(this)}
                                        onError={Graph.onFilesError}
                                        accepts={['.json']}
                                        multiple
                                        maxFiles={3}
                                        maxFileSize={10000000}
                                        minFileSize={0}
                                        clickable>
                                        Drop files here or click to upload
                                    </Files>
                                </div>
                                <div className="files">
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
                            </div>
                            <div>
                                <Button variant='outline-primary'
                                    onClick={this
                                    .saveGraph
                                    .bind(this)}>Save</Button>
                            </div>
                        </div>
                    </Col>

                </Row>
            </Container>
        );
    }

}

function isGraphAcyclic(graph) {
    let directedEdges = graph
        .edges
        .map(el => [el.source, el.target]);

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
