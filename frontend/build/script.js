"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const document_1 = require("./document");
const render_1 = require("./render");
const canvasrender = new render_1.CanvasRender();
const svgrender = new render_1.SVGRender();
const sdd = new document_1.SimpleDrawDocument();
const c1 = sdd.createCircle(50, 50, 30);
const r1 = sdd.createRectangle(10, 10, 80, 80);
const r2 = sdd.createRectangle(30, 30, 40, 40);
/* const s1 = sdd.createSelection(c1, r1, r2)
sdd.translate(s1, 10, 10) */
sdd.draw(canvasrender);
sdd.draw(svgrender);
//# sourceMappingURL=script.js.map