"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const shape_1 = require("./shape");
class SVGRender {
    constructor() {
        this.svg = document.getElementById('svgcanvas');
    }
    draw(...objs) {
        for (const shape of objs) {
            if (shape instanceof shape_1.Rectangle) {
                const e = document.createElementNS("http://www.w3.org/2000/svg", "rect");
                e.setAttribute('style', 'stroke: black; fill: white');
                e.setAttribute('x', shape.x.toString());
                e.setAttribute('y', shape.y.toString());
                e.setAttribute('width', shape.width.toString());
                e.setAttribute('height', shape.height.toString());
                this.svg.appendChild(e);
            }
        }
    }
}
exports.SVGRender = SVGRender;
class CanvasRender {
    constructor() {
        const canvas = document.getElementById('canvas');
        this.ctx = canvas.getContext('2d');
    }
    draw(...objs) {
        for (const shape of objs) {
            if (shape instanceof shape_1.Circle) {
                this.ctx.ellipse(shape.x, shape.y, shape.radius, shape.radius, 0, 0, 2 * Math.PI);
                this.ctx.stroke();
            }
            else if (shape instanceof shape_1.Rectangle) {
                this.ctx.strokeRect(shape.x, shape.y, shape.width, shape.height);
            }
        }
    }
}
exports.CanvasRender = CanvasRender;
//# sourceMappingURL=render.js.map