"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class Shape {
    constructor(x, y) {
        this.x = x;
        this.y = y;
    }
    translate(xd, yd) {
        this.x += xd;
        this.y += yd;
    }
}
exports.Shape = Shape;
class Rectangle extends Shape {
    constructor(x, y, width, height) {
        super(x, y);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
exports.Rectangle = Rectangle;
class Circle extends Shape {
    constructor(x, y, radius) {
        super(x, y);
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
}
exports.Circle = Circle;
//# sourceMappingURL=shape.js.map