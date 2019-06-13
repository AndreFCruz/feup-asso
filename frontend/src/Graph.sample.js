import {
  SOURCE_TYPE,
  HANDLER_TYPE,
  SINK_TYPE,
  STANDARD_EDGE_TYPE,
} from "./Graph.configs";

// TODO Nodes should have property "node.subtype" set to their function (e.g. FILE_WRITER)
export const sample = {
  nodes: [
    {
      id: 'source.INTEGER_GENERATOR',
      title: 'INTEGER_GENERATOR',
      type: SOURCE_TYPE,
      x: 100,
      y: 0,
    },
    {
      id: 'source.STRING_GENERATOR',
      title: 'STRING_GENERATOR',
      type: SOURCE_TYPE,
      x: 100,
      y: 200,
    },
    {
      id: 'handler.MD5_CONVERTER',
      title: 'MD5_CONVERTER',
      type: HANDLER_TYPE,
      x: 300,
      y: 0,
    },
    {
      id: 'handler.UPPER_CASE_CONVERTER',
      title: 'UPPER_CASE_CONVERTER',
      type: HANDLER_TYPE,
      x: 300,
      y: 200,
    },
    {
      id: 'sink.FILE_WRITER',
      title: 'FILE_WRITER',
      type: SINK_TYPE,
      x: 500,
      y: 0,
    },
    {
      id: 'sink.PRINTER',
      title: 'PRINTER',
      type: SINK_TYPE,
      x: 500,
      y: 200,
    },
  ],
  edges: [
    {
      source: 'source.INTEGER_GENERATOR',
      target: 'handler.MD5_CONVERTER',
      type: STANDARD_EDGE_TYPE,
    },
    {
      source: 'source.STRING_GENERATOR',
      target: 'handler.MD5_CONVERTER',
      type: STANDARD_EDGE_TYPE,
    },
    {
      source: 'handler.MD5_CONVERTER',
      target: 'sink.FILE_WRITER',
      type: STANDARD_EDGE_TYPE,
    },
    {
      source: 'source.STRING_GENERATOR',
      target: 'handler.UPPER_CASE_CONVERTER',
      type: STANDARD_EDGE_TYPE,
    },
    {
      source: 'handler.UPPER_CASE_CONVERTER',
      target: 'sink.PRINTER',
      type: STANDARD_EDGE_TYPE,
    },
  ],
}
