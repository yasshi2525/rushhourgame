/* 
 * The MIT License
 *
 * Copyright 2017 yasshi2525 <https://twitter.com/yasshi2525>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

var pixi = require('pixi.js');

exports.init = function (params) {
    initPixi(params);
    initEventHandler(params);
};

function initPixi(params) {
    var renderer = pixi.autoDetectRenderer();
    $('#' + params.canvas).get(0).appendChild(renderer.view);

    renderer.backgroundColor = 0x808080;
    pixi.loader
            .add([
                "resources/image/s_absorber.png",
                "resources/image/s_distributer.png",
                "resources/image/s_station.png",
                "resources/image/s_train.png"])
            .load();
}

function initEventHandler(params) {
    $canvas = $('#' + params.canvas);
    $mouseX = $('#' + params.mouseX);
    $mouseY = $('#' + params.mouseY);
    $canvas.on({
        'click': function (event) {
            var mouseX = event.originalEvent.offsetX;
            var mouseY = event.originalEvent.offsetY;
            $mouseX.val(mouseX);
            $mouseY.val(mouseY);
        }
    });
}

function onClickCanvas(event) {

}