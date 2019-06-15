import axios from "axios";
// import * as React from 'react';
import React from 'react';

export const NODE_KEY = 'id'; // Key used to identify nodes

export async function makeGraphConfigObject() {

  // Fetch available node types from backend server
  // let nodeTypesResponse = await axios.get(process.env.REACT_APP_API_URL + '/node-types');

  // NOTE Sample response from backend server
  let nodeTypesResponse = {"sources":["INTEGER_GENERATOR","STRING_GENERATOR"],"sinks":["FILE_WRITER","PRINTER"],"handlers":["MD5_CONVERTER","UPPER_CASE_CONVERTER"]};
  console.log(nodeTypesResponse);

  return {
    EdgeTypes: makeEdgeTypesObject(),
    NodeTypes: makeNodeTypesObject(),
    NodeSubtypes: makeNodeSubtypesObject(nodeTypesResponse)
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
  for (const sourceType of types.sources) {
    retObj[sourceType] = {
      shape: SourceNodeShape,
      shapeId: '#' + SOURCE_TYPE,
      nodeType: SOURCE_TYPE,
    };
  }

  for (const handlerType of types.handlers) {
    retObj[handlerType] = {
      shape: HandlerNodeShape,
      shapeId: '#' + HANDLER_TYPE,
      nodeType: HANDLER_TYPE,
    };
  }

  for (const sinkType of types.sinks) {
    retObj[sinkType] = {
      shape: SinkNodeShape,
      shapeId: '#' + SINK_TYPE,
      nodeType: SINK_TYPE,
    };
  }

  console.log('NODE SUB-TYPES:');
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
    <circle cx="80" cy="80" r="70" />
  </symbol>
);

const HandlerNodeShape = (
  <symbol viewBox="0 0 80 80" id={HANDLER_TYPE}>
    <circle cx="40" cy="40" r="35" />
  </symbol>
);

const SinkNodeShape = (
  <symbol viewBox="0 0 120 120" id={SINK_TYPE}>
    <circle cx="60" cy="60" r="55" />
  </symbol>
);

const StandardEdgeShape = (
  <symbol viewBox="0 0 50 50" id={STANDARD_EDGE_TYPE}>
    <circle cx="25" cy="25" r="8" fill="currentColor" />
  </symbol>
);
