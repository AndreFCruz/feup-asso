import axios from "axios";
import React from 'react';

export const NODE_KEY = 'id'; // Key used to identify nodes

let DEFAULT_NODE_TYPES = {
    "sources": {
        "INTEGER_GENERATOR": {},
        "STRING_GENERATOR": {},
        "FETCH_URL": {
            "settings": ["url"]
        },
    },
    "sinks": {
        "FILE_WRITER": {
            "settings": ["path"]
        },
        "PRINTER": {},
    },
    "handlers": {
        "MD5_CONVERTER": {},
        "UPPER_CASE_CONVERTER": {},
    },
};

export async function makeGraphConfigObject() {

    // Fetch available node types from backend server
    let nodeTypesResponse = await axios.get(process.env.REACT_APP_API_URL + '/node-types')
        .then(res => res.data)
        .catch(_ => DEFAULT_NODE_TYPES);

    return {
        EdgeTypes: makeEdgeTypesObject(),
        NodeTypes: makeNodeTypesObject(),
        NodeSubtypes: makeNodeSubtypesObject(nodeTypesResponse),
        DefaultNodesTypes: nodeTypesResponse
    };
}

function makeNodeTypesObject() {
    return {
        sourceNode: {
            shape: SourceNodeShape,
            shapeId: '#' + SOURCE_TYPE,
            typeText: 'Source'
        },
        handlerNode: {
            shape: HandlerNodeShape,
            shapeId: '#' + HANDLER_TYPE,
            typeText: 'Handler'
        },
        sinkNode: {
            shape: SinkNodeShape,
            shapeId: '#' + SINK_TYPE,
            typeText: 'Sink'
        },
    };
}

function makeEdgeTypesObject() {
    return {
        standardEdge: {
            shape: StandardEdgeShape,
            shapeId: '#' + STANDARD_EDGE_TYPE,
        },
    };
}

function makeNodeSubtypesObject(types) {
    let retObj = {};
    for (const sourceType of Object.getOwnPropertyNames(types.sources)) {
        retObj[sourceType] = {
            shape: SourceNodeShape,
            shapeId: '#' + SOURCE_TYPE,
            nodeType: SOURCE_TYPE,
            settings: types.sources[sourceType].settings || [],
            inputType: types.sources[sourceType].inputType || "",
            outputType: types.sources[sourceType].outputType || "",
        };
    }

    for (const handlerType of Object.getOwnPropertyNames(types.handlers)) {
        retObj[handlerType] = {
            shape: HandlerNodeShape,
            shapeId: '#' + HANDLER_TYPE,
            nodeType: HANDLER_TYPE,
            settings: types.handlers[handlerType].settings || [],
            inputType: types.handlers[handlerType].inputType || "",
            outputType: types.handlers[handlerType].outputType || "",
        };
    }

    for (const sinkType of Object.getOwnPropertyNames(types.sinks)) {
        retObj[sinkType] = {
            shape: SinkNodeShape,
            shapeId: '#' + SINK_TYPE,
            nodeType: SINK_TYPE,
            settings: types.sinks[sinkType].settings || [],
            inputType: types.sinks[sinkType].inputType || "",
            outputType: types.sinks[sinkType].outputType || "",
        };
    }

    console.log('** NODE SUB-TYPES **');
    console.log(retObj);
    return retObj;
}

// These keys are arbitrary (but must match the config)
// However, GraphView renders text differently for empty types
// so this has to be passed in if that behavior is desired.

// Node Types
export const SOURCE_TYPE = 'sourceNode';
export const HANDLER_TYPE = 'handlerNode';
export const SINK_TYPE = 'sinkNode';
export const nodeTypes = [SOURCE_TYPE, HANDLER_TYPE, SINK_TYPE];

// Edge Types
export const STANDARD_EDGE_TYPE = 'standardEdge';
export const edgeTypes = [STANDARD_EDGE_TYPE];


const SourceNodeShape = (
    <symbol viewBox="0 0 160 160" id={SOURCE_TYPE}>
        <circle cx="80" cy="80" r="70"/>
    </symbol>
);

const HandlerNodeShape = (
    <symbol viewBox="0 0 80 80" id={HANDLER_TYPE}>
        <circle cx="40" cy="40" r="35"/>
    </symbol>
);

const SinkNodeShape = (
    <symbol viewBox="0 0 120 120" id={SINK_TYPE}>
        <circle cx="60" cy="60" r="55"/>
    </symbol>
);

const StandardEdgeShape = (
    <symbol viewBox="0 0 50 50" id={STANDARD_EDGE_TYPE}>
        <circle cx="25" cy="25" r="8" fill="currentColor"/>
    </symbol>
);
