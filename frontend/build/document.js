"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const actions_1 = require("./actions");
const undo_1 = require("./undo");
class SimpleDrawDocument {
    constructor() {
        this.objects = new Array();
        this.undoManager = new undo_1.UndoManager();
    }
    undo() {
        this.undoManager.undo();
    }
    redo() {
        this.undoManager.redo();
    }
    draw(render) {
        // this.objects.forEach(o => o.draw(ctx))
        render.draw(...this.objects);
    }
    add(r) {
        this.objects.push(r);
    }
    do(a) {
        this.undoManager.onActionDone(a);
        return a.do();
    }
    createRectangle(x, y, width, height) {
        return this.do(new actions_1.CreateRectangleAction(this, x, y, width, height));
    }
    createCircle(x, y, radius) {
        return this.do(new actions_1.CreateCircleAction(this, x, y, radius));
    }
    translate(s, xd, yd) {
        return this.do(new actions_1.TranslateAction(this, s, xd, yd));
    }
}
exports.SimpleDrawDocument = SimpleDrawDocument;
//# sourceMappingURL=document.js.map