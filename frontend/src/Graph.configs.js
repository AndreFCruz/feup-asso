import axios from "axios";
import * as React from 'react';

export const NODE_KEY = 'id'; // Key used to identify nodes

export function makeGraphConfigObject() {

  // Fetch available node types from backend server
  // axios.get(process.env.REACT_APP_API_URL + '/node-types')
  //   .then(response => {
  //     console.log(response.data);
  //   });

  return {
    EdgeTypes: makeEdgeTypesObject(),
    NodeTypes: makeNodeTypesObject(),
    NodeSubtypes: makeNodeSubtypesObject()
  };
}

function makeNodeTypesObject() {
  return {
    emptyNode: {
      shape: EmptyNodeShape,
      shapeId: '#emptyNode',
      typeText: 'None'
    },
    empty: {
      shape: CustomEmptyShape,
      shapeId: '#empty',
      typeText: 'None'
    },
    special: {
      shape: SpecialShape,
      shapeId: '#special',
      typeText: 'Special'
    },
    skinny: {
      shape: SkinnyShape,
      shapeId: '#skinny',
      typeText: 'Skinny'
    },
    poly: {
      shape: PolyShape,
      shapeId: "#poly",
      typeText: 'Poly'
    }
  };
}

function makeEdgeTypesObject() {
  return {
    standardEdge: {
      shape: EmptyEdgeShape,
      shapeId: '#emptyEdge'
    },
    specialEdge: {
      shape: SpecialEdgeShape,
      shapeId: '#specialEdge'
    }
  };
}

function makeNodeSubtypesObject() {
  return {};
}

// These keys are arbitrary (but must match the config)
// However, GraphView renders text differently for empty types
// so this has to be passed in if that behavior is desired.
export const EMPTY_TYPE = 'customEmpty'; // Empty node type
export const POLY_TYPE = 'poly';
export const SPECIAL_TYPE = 'special';
export const SKINNY_TYPE = 'skinny';
export const EMPTY_EDGE_TYPE = 'emptyEdge';
export const SPECIAL_EDGE_TYPE = 'specialEdge';

export const nodeTypes = [EMPTY_TYPE, POLY_TYPE, SPECIAL_TYPE, SKINNY_TYPE];
export const edgeTypes = [EMPTY_EDGE_TYPE, SPECIAL_EDGE_TYPE];


const EmptyNodeShape = (
  <symbol viewBox="0 0 154 154" width="154" height="154" id="emptyNode">
    <circle cx="77" cy="77" r="76" />
  </symbol>
);

const CustomEmptyShape = (
  <symbol viewBox="0 0 100 100" id={EMPTY_TYPE}>
    <circle cx="50" cy="50" r="45" />
  </symbol>
);

const SpecialShape = (
  <symbol viewBox="-27 0 154 154" id={SPECIAL_TYPE} width="154" height="154">
    <rect transform="translate(50) rotate(45)" width="109" height="109" />
  </symbol>
);

const PolyShape = (
  <symbol viewBox="0 0 88 72" id={POLY_TYPE} width="88" height="88">
    <path d="M 0 36 18 0 70 0 88 36 70 72 18 72Z"></path>
  </symbol>
);

const SkinnyShape = (
  <symbol viewBox="0 0 154 54" width="154" height="54" id={SKINNY_TYPE}>
    <rect x="0" y="0" rx="2" ry="2" width="154" height="54" />
  </symbol>
);

const EmptyEdgeShape = (
  <symbol viewBox="0 0 50 50" id={EMPTY_EDGE_TYPE}>
    <circle cx="25" cy="25" r="8" fill="currentColor" />
  </symbol>
);

const SpecialEdgeShape = (
  <symbol viewBox="0 0 50 50" id={SPECIAL_EDGE_TYPE}>
    <rect transform="rotate(45)" x="27.5" y="-7.5" width="15" height="15" fill="currentColor" />
  </symbol>
);

export const SAMPLE_GRAPH_CONFIG = {
  EdgeTypes: {
    standardEdge: {
      shape: EmptyEdgeShape,
      shapeId: '#emptyEdge'
    },
    specialEdge: {
      shape: SpecialEdgeShape,
      shapeId: '#specialEdge'
    }
  },
  NodeSubtypes: { // Ignoring node subtypes...
    // specialChild: {
    //   shape: SpecialChildShape,
    //   shapeId: '#specialChild'
    // }
  },
  NodeTypes: {
    emptyNode: {
      shape: EmptyNodeShape,
      shapeId: '#emptyNode',
      typeText: 'None'
    },
    empty: {
      shape: CustomEmptyShape,
      shapeId: '#empty',
      typeText: 'None'
    },
    special: {
      shape: SpecialShape,
      shapeId: '#special',
      typeText: 'Special'
    },
    skinny: {
      shape: SkinnyShape,
      shapeId: '#skinny',
      typeText: 'Skinny'
    },
    poly: {
      shape: PolyShape,
      shapeId: "#poly",
      typeText: 'Poly'
    }
  }
};
