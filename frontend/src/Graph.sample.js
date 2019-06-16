import {HANDLER_TYPE, SINK_TYPE, SOURCE_TYPE, STANDARD_EDGE_TYPE,} from "./Graph.configs";

// TODO Nodes should have property "node.subtype" set to their function (e.g. FILE_WRITER)
export const sample = {
    "nodes": [
        {
            "id": "urceNode1",
            "title": "INTEGER_GENERATOR-1",
            "type": SOURCE_TYPE,
            "subtype": "INTEGER_GENERATOR",
            "x": 30,
            "y": 30,
            "settings": {}
        },
        {
            "id": "ndlerNode2",
            "title": "MD5_CONVERTER-2",
            "type": HANDLER_TYPE,
            "subtype": "MD5_CONVERTER",
            "x": 230,
            "y": 30,
            "settings": {}
        },
        {
            "id": "urceNode3",
            "title": "STRING_GENERATOR-3",
            "type": SOURCE_TYPE,
            "subtype": "STRING_GENERATOR",
            "x": 30,
            "y": 230,
            "settings": {}
        },
        {
            "id": "ndlerNode4",
            "title": "UPPER_CASE_CONVERTER-4",
            "type": HANDLER_TYPE,
            "subtype": "UPPER_CASE_CONVERTER",
            "x": 230,
            "y": 230,
            "settings": {}
        },
        {
            "id": "nkNode5",
            "title": "FILE_WRITER-5",
            "type": SINK_TYPE,
            "subtype": "FILE_WRITER",
            "x": 430,
            "y": 30,
            "settings": {"path": `./Files/${String(Math.floor(Math.random() * 9e15))}`}
        },
        {
            "id": "nkNode6",
            "title": "PRINTER-6",
            "type": SINK_TYPE,
            "subtype": "PRINTER",
            "x": 430,
            "y": 230,
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