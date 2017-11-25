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

/**
 * 当初, グローバル変数を作らず、引数に渡すことで実現しようとした。
 * しかし、コールバック関数内で値を参照することができなかったため、
 * グローバル変数を定義することで解決することにした。
 * @type type
 */
var scope;

/**
 * jQueryオブジェクトを格納する場合、接頭辞$をつける。
 * @param {type} param
 * @returns {undefined}
 */
exports.init = function (param) {
    scope = param;
    initEventHandler();

    // 画像ロード後、スプライトを表示
    initPixi();
};

function initPixi() {

    var renderer = pixi.autoDetectRenderer();
    scope.$canvas.get(0).appendChild(renderer.view); // get(0)がないとダメ

    scope.renderer = renderer;
    scope.stage = new pixi.Container();

    // 複数形にすると、要素名と一致しなく不便だったので、単数形
    scope.sprites = {
        'company': {},
        'residence': {},
        'railedge': {},
        'station': {},
        'stepforhuman': {}
    };

    // 画像をロードしたあと、スプライトを表示
    pixi.loader
            .add('company', 'resources/image/s_company.png')
            .add('residence', 'resources/image/s_residence.png')
            .add('station', 'resources/image/s_station.png')
            .add('train', 'resources/image/s_train.png')
            .load(registerSprite);
}

function initEventHandler() {
    scope.$canvas.on({
        'click': function (event) {
            var mouseX = event.originalEvent.offsetX;
            var mouseY = event.originalEvent.offsetY;
            scope.$mouseX.val(mouseX);
            scope.$mouseY.val(mouseY);
        }
    });
}

function registerSprite() {
    // 画像つきリソースを作成する。
    var resources = {
        'company': {},
        'residence': {},
        'station': {}
    };

    for (var name in resources) {
        // クラス名 : .リソース名
        $('.' + name).each(function (i, elm) {
            scope.sprites[name][$(elm).attr('id')]
                    = stageResourceSprite(name, $(elm));
        });
    };

    // 線タイプのリソースを作成すうｒ
    resources = {
        railedge: {
            color: 0xaaaaaa,
            scale: 3
        },
        stepforhuman: {
            color: 0x888888,
            scale: 0
        }
    };

    for (var name in resources) {
        // クラス名 : .リソース名
        $('.' + name).each(function (i, elm) {
            scope.sprites[name][$(elm).attr('id')]
                    = stageLine({
                        fromx: parseFloat($(elm).data('from-x')),
                        fromy: parseFloat($(elm).data('from-y')),
                        tox: parseFloat($(elm).data('to-x')),
                        toy: parseFloat($(elm).data('to-y'))
                    }, resources[name].color, resources[name].scale);
        });
    };

    scope.renderer.render(scope.stage);
}

function stageResourceSprite(type, $elm) {
    var obj = createSprite(
            type,
            parseFloat($elm.data('x')),
            parseFloat($elm.data('y')));
    scope.stage.addChild(obj);
    return obj;
}

function createSprite(type, x, y) {
    var sprite = new pixi.Sprite(pixi.loader.resources[type].texture);
    sprite.anchor.set(0.5, 0.5);
    sprite.alpha = 1;
    sprite.position.set(x, y);
    return sprite;
}

/**
 * 路線のlineではなく、線のline
 * @param {type} line
 * @param {type} color
 * @param {type} scale
 * @returns {objine.obj|nm$_gameview.pixi.Graphics|nm$_gameview.stageLine.sprite}
 */
function stageLine(line, color, scale) {
    var obj = new pixi.Graphics();

    line = slideEdge(line, scale);

    obj
            .lineStyle(3, color)
            .moveTo(line.fromx, line.fromy)
            .lineTo(line.tox, line.toy);

    scope.stage.addChild(obj);
    return obj;
}

/**
 * 
 * @param {type} line { fromx : x1, fromy : y1, tox : x2, toy : y2 }
 * @param {type} scale
 * @returns {nm$_gameview.slideEdge.gameviewAnonym$1}
 */
function slideEdge(line, scale) {
    var theta = Math.atan2(line.toy - line.fromy, line.tox - line.fromx) - Math.PI / 2;
    return {
        fromx: line.fromx + Math.cos(theta) * scale,
        fromy: line.fromy + Math.sin(theta) * scale,
        tox: line.tox + Math.cos(theta) * scale,
        toy: line.toy + Math.sin(theta) * scale
    };
}