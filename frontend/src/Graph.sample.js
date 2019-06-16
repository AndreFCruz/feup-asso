import {HANDLER_TYPE, SINK_TYPE, SOURCE_TYPE, STANDARD_EDGE_TYPE,} from "./Graph.configs";

// TODO Nodes should have property "node.subtype" set to their function (e.g. FILE_WRITER)
export const sample = {
    "nodes": [
        {
            "id": "urceNode1",
            "title": "INTEGER_GENERATOR-1",
            "type": SOURCE_TYPE,
            "subtype": "INTEGER_GENERATOR",
            "x": 30.303024291992188,
            "y": 60.6060791015625,
            "settings": {}
        },
        {
            "id": "ndlerNode2",
            "title": "MD5_CONVERTER-2",
            "type": HANDLER_TYPE,
            "subtype": "MD5_CONVERTER",
            "x": 420.6060485839844,
            "y": 63.030303955078125,
            "settings": {}
        },
        {
            "id": "urceNode3",
            "title": "STRING_GENERATOR-3",
            "type": SOURCE_TYPE,
            "subtype": "STRING_GENERATOR",
            "x": 35.15150451660156,
            "y": 276.3636474609375,
            "settings": {}
        },
        {
            "id": "ndlerNode4",
            "title": "UPPER_CASE_CONVERTER-4",
            "type": HANDLER_TYPE,
            "subtype": "UPPER_CASE_CONVERTER",
            "x": 416.9696960449219,
            "y": 267.8787841796875,
            "settings": {}
        },
        {
            "id": "nkNode5",
            "title": "FILE_WRITER-5",
            "type": SINK_TYPE,
            "subtype": "FILE_WRITER",
            "x": 712.727294921875,
            "y": 66.66667175292969,
            "settings": {"path": `./Files/${String(Math.floor(Math.random() * 9e15))}`}
        },
        {
            "id": "nkNode6",
            "title": "PRINTER-6",
            "type": SINK_TYPE,
            "subtype": "PRINTER",
            "x": 711.51513671875,
            "y": 269.0909118652344,
            "settings": {}
        }
    ],
    "edges": [
        {
            "source": "urceNode1",
            "target": "ndlerNode2",
            "type": STANDARD_EDGE_TYPE
        },
        {
            "source": "urceNode3",
            "target": "ndlerNode2",
            "type": STANDARD_EDGE_TYPE
        },
        {
            "source": "ndlerNode2",
            "target": "nkNode5",
            "type": STANDARD_EDGE_TYPE
        },
        {
            "source": "urceNode3",
            "target": "ndlerNode4",
            "type": STANDARD_EDGE_TYPE
        },
        {
            "source": "ndlerNode4",
            "target": "nkNode6",
            "type": STANDARD_EDGE_TYPE
        }
    ]
};