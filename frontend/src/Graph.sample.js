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
      id: 'source.REST_SERVER',
      title: 'REST_SERVER',
      type: SOURCE_TYPE,
      x: 100,
      y: 0,
    },
    {
      id: 'source.FILE_READER',
      title: 'FILE_READER',
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
      id: 'handler.TO_UPPER_CASE',
      title: 'TO_UPPER_CASE',
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
      source: 'source.REST_SERVER',
      target: 'handler.MD5_CONVERTER',
      type: STANDARD_EDGE_TYPE,
    },
    {
      source: 'source.FILE_READER',
      target: 'handler.MD5_CONVERTER',
      type: STANDARD_EDGE_TYPE,
    },
    {
      source: 'handler.MD5_CONVERTER',
      target: 'handler.TO_UPPER_CASE',
      type: STANDARD_EDGE_TYPE,
    },
    {
      source: 'handler.TO_UPPER_CASE',
      target: 'sink.FILE_WRITER',
      type: STANDARD_EDGE_TYPE,
    },
    {
      source: 'handler.TO_UPPER_CASE',
      target: 'sink.PRINTER',
      type: STANDARD_EDGE_TYPE,
    },
  ],
}
