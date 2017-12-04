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

var spriteResources = {
    'company': {},
    'residence': {},
    'station': {}
};

var lineResources = {
    railedge: {
        color: 0xaaaaaa,
        slide: 3,
        scale: 3
    }
};

/**
 * jQueryオブジェクトを格納する場合、接頭辞$をつける。
 * @param {type} param
 * @returns {undefined}
 */
exports.init = function (param) {
    $(document).data('scope', param);

    // 画像ロード後、スプライトを表示
    initPixi();
};

function initPixi() {
    var scope = $(document).data('scope');

    var renderer = pixi.autoDetectRenderer(512, 512);
    scope.$gameview.get(0).appendChild(renderer.view); // get(0)がないとダメ
    scope.$canvas = $('#gameview canvas');
    initEventHandler(); //イベントハンドラ登録

    scope.renderer = renderer;
    scope.stage = new pixi.Container();

    // 複数形にすると、要素名と一致しなく不便だったので、単数形
    scope.graphics = {
        'company': {},
        'residence': {},
        'railedge': {},
        'station': {},
        'stepforhuman': {}
    };

    // 画像をロードしたあと、イベントハンドラスプライトを表示
    pixi.loader
            .add('company', 'resources/image/s_company.png')
            .add('residence', 'resources/image/s_residence.png')
            .add('station', 'resources/image/s_station.png')
            .add('train', 'resources/image/s_train.png')
            .load(fetchGraphics);
}

function initEventHandler() {
    var scope = $(document).data('scope');
    scope.$canvas.on({
        // 当初 clickイベント時にクリックメニューを表示するようにしていたが、
        // ドラッグの終わりにもメニューが表示されてしまった。
        // そこで onDragEnd時に移動があったか判定するようにした。
        // マップスクロール用。
        // PIXI側で実現しようとしたが、スプライトを用意する必要があるため、
        // canvas要素側で実現することにした。
        'mousedown': onDragStart,
        'touchstart': onDragStart,
        'mouseup': onDragEnd,
        'mouseout': onDragEnd,
        'touchend': onDragEnd,
        'mousemove': onDragMove,
        'touchmove': onDragMove
    });
}

// fetchGraphics()にすると、ajaxでよびだされない
fetchGraphics = function () {
    var scope = $(document).data('scope');

    // 既存のリソースにマークをつける
    // 更新後、マークが残っていたら削除する。
    for (var name in scope.graphics) {
        for (var key in scope.graphics[name]) {
            scope.graphics[name][key].old = true;
        }
    }

    // 画像つきリソースを作成する。
    for (var name in spriteResources) {
        // クラス名 : .リソース名
        $('.' + name).each(function (i, elm) {

            if (scope.graphics[name][$(elm).attr('id')]) {
                // 更新
                scope.graphics[name][$(elm).attr('id')].old = false;
                updateSprite(
                        scope.graphics[name][$(elm).attr('id')],
                        $(elm));
            } else {
                // 新規作成
                scope.graphics[name][$(elm).attr('id')]
                        = stageResourceSprite(name, $(elm));
            }
        });
    }

    // 線タイプのリソースを作成する
    for (var name in lineResources) {
        // クラス名 : .リソース名
        $('.' + name).each(function (i, elm) {
            if (scope.graphics[name][$(elm).attr('id')]) {
                // 更新する
                // Graphicsの移動の仕方が分からなkったので、リライトする
                scope.stage.removeChild(scope.graphics[name][$(elm).attr('id')]);
                scope.graphics[name][$(elm).attr('id')]
                        = stageLine($(elm), lineResources[name]);
            } else {
                // 新規作成
                scope.graphics[name][$(elm).attr('id')]
                        = stageLine($(elm), lineResources[name]);
            }
        });
    }

    // divタグ中に存在しないリソースを削除
    for (var name in scope.graphics) {
        for (var key in scope.graphics[name]) {
            if (scope.graphics[name][key].old) {
                scope.stage.removeChild(scope.graphics[name][key]);
                delete scope.graphics[name][key];
            }
        }
    }
    scope.renderer.render(scope.stage);
};

function stageResourceSprite(type, $elm) {
    var scope = $(document).data('scope');
    var pos = toViewPos(
            parseFloat($elm.data('x')),
            parseFloat($elm.data('y')));

    var obj = new pixi.Sprite(pixi.loader.resources[type].texture);
    obj.anchor.set(0.5, 0.5);
    obj.alpha = 1;
    obj.position.set(pos.x, pos.y);

    scope.stage.addChild(obj);
    return obj;
}

function updateSprite(sprite, $elm) {
    var pos = toViewPos(
            parseFloat($elm.data('x')),
            parseFloat($elm.data('y')));

    sprite.position.set(pos.x, pos.y);
}

/**
 * 路線のlineではなく、線のline
 * @param {type} $elm
 * @param {type} opts
 * @returns 
 */
function stageLine($elm, opts) {
    var scope = $(document).data('scope');
    var obj = new pixi.Graphics();

    var from = toViewPos(
            parseFloat($elm.data('from-x')),
            parseFloat($elm.data('from-y')));
    var to = toViewPos(
            parseFloat($elm.data('to-x')),
            parseFloat($elm.data('to-y')));

    var line = slideEdge(from, to, opts.slide);

    obj.lineStyle(opts.scale, opts.color)
            .moveTo(line.fromx, line.fromy)
            .lineTo(line.tox, line.toy);

    scope.stage.addChild(obj);
    return obj;
}

function slideEdge(from, to, scale) {
    var theta = Math.atan2(to.y - from.y, to.x - from.x) - Math.PI / 2;
    return {
        fromx: from.x + Math.cos(theta) * scale,
        fromy: from.y + Math.sin(theta) * scale,
        tox: to.x + Math.cos(theta) * scale,
        toy: to.y + Math.sin(theta) * scale
    };
}

/**
 * サーバ上のパスを画面上の座標に変換する
 * @param {type} x
 * @param {type} y
 * @returns {nm$_gameview.toViewPos.gameviewAnonym$2}
 */
function toViewPos(x, y) {
    var scope = $(document).data('scope');
    return {
        x: (x - scope.$centerX.val())
                * scope.renderer.width
                * Math.pow(2, -$('#scale').text())
                + scope.renderer.width / 2,
        y: (y - scope.$centerY.val())
                * scope.renderer.height
                * Math.pow(2, -$('#scale').text())
                + scope.renderer.height / 2
    };
}
/**
 * スライダーを動かした時、リアルタイムで拡大、縮小できるようにする
 * @param {type} event
 * @param {type} ui
 * @returns {undefined}
 */
handleSlide = function (event, ui) {
    $('#scale').text(ui.value / 100);
    fetchGraphics();
};

function onDragStart(event) {
    var scope = $(document).data('scope');
    this.moving = false;
    this.dragging = true;
    this.startGamePos = {
        x: parseFloat(scope.$centerX.val()),
        y: parseFloat(scope.$centerY.val())
    };
    this.startPosition = {
        x: (event.pageX) ? event.pageX : event.originalEvent.touches[0].pageX,
        y: (event.pageY) ? event.pageY : event.originalEvent.touches[0].pageY
    };
}

function onDragEnd(event) {
    var scope = $(document).data('scope');
    
    if (!this.moving) {
        // クリックと判定した
        // マウスの座標をゲーム上の座標に変換する
        var mouseX = event.originalEvent.offsetX;
        var mouseY = event.originalEvent.offsetY;

        var gamePos = toGamePos(mouseX, mouseY);

        scope.$clickX.val(gamePos.x);
        scope.$clickY.val(gamePos.y);
        fireClickMenu([
            {name: 'gamePos.x', value: gamePos.x},
            {name: 'gamePos.y', value: gamePos.y}]);
    } else {
        // ドラッグと反映した
    }

    this.moving = false;
    this.dragging = false;
    this.startGamePos = null;
    this.startPosition = null;
}

/**
 * ドラッグ時、新しくなるcenterX, centerYを求める
 * @param {type} event
 * @returns {undefined}
 */
function onDragMove(event) {
    var scope = $(document).data('scope');
    this.moving = true;
    if (this.dragging) {
        var newCenterPos = toGamePosByDiff(
                this.startGamePos, this.startPosition, toViewPosFromMouse(event));

        scope.$centerX.val(newCenterPos.x);
        scope.$centerY.val(newCenterPos.y);
        fetchGraphics();
    }
}

toViewPosFromMouse = function (event) {
    return {
        x: (event.pageX) ? event.pageX : event.originalEvent.touches[0].pageX,
        y: (event.pageY) ? event.pageY : event.originalEvent.touches[0].pageY
    };
};

toGamePosByDiff = function (startGamePos, startViewPos, newViewPos) {
    var scope = $(document).data('scope');
    return {
        x: startGamePos.x
                - (newViewPos.x - startViewPos.x)
                * Math.pow(2, $('#scale').text()) / scope.renderer.width,
        y: startGamePos.y
                - (newViewPos.y - startViewPos.y)
                * Math.pow(2, $('#scale').text()) / scope.renderer.height
    };
};

toGamePos = function (x, y) {
    var scope = $(document).data('scope');
    // - 0.5 するのは画面の中央がcenterXに対応するため
    return {
        x: (x / scope.renderer.width - 0.5)
                * Math.pow(2, $('#scale').text())
                + parseFloat(scope.$centerX.val()),
        y: (y / scope.renderer.height - 0.5)
                * Math.pow(2, $('#scale').text())
                + parseFloat(scope.$centerY.val())
    };
};