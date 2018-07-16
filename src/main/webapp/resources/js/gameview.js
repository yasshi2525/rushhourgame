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

/** @module gameview */

/**
 * 座標を表すクラス
 * @typedef Point
 * @type {Object}
 * @property {number} x x座標
 * @property {number} y y座標 
 */

var pixi = require('pixi.js');

var consts = {
    round: 20,
    background: {
        width: 1,
        color: 0xffffff,
        alpha: 0.25
    },
    slider: {
        min: 0.0,
        max: 16.0
    }
};

var spriteResources = {
    'residence': {},
    'company': {},
    'station': {},
    'lonelyrailnode': {}
};

var lineResources = {
    railedge: {
        my: {
            slide: 5,
            scale: 3,
            alpha: 0.5
        },
        other: {
            slide: 3,
            scale: 1,
            alpha: 0.5
        }
    },
    linestep: {
        my: {
            slide: 15,
            scale: 1,
            alpha: 0.5
        },
        other: {
            slide: 10,
            scale: 1,
            alpha: 0.5
        }
    }
};

var movableSprites = {
    train: {}
};

var movableCircles = {
    human: {
        color: 0xff69b4,
        radius: 3,
        alpha: 0.5
    }
};

var tempResources = {
    neighborNode: {
        color: 0xbf7fff,
        radius: consts.round,
        alpha: 0.5
    },
    tailNode: {
        color: 0xa0a0a0,
        radius: 10,
        alpha: 0.5
    },
    cursor: {
        color: 0x7fff7f,
        radius: consts.round / 2,
        alpha: 0.5
    },
    extendEdge: {
        color: 0x7fff7f,
        width: 4,
        alpha: 0.5
    },
    neighborEdge: {
        color: 0xbf7fff,
        slide: 5,
        scale: 5,
        alpha: 0.5
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

initPixi = function () {
    var scope = $(document).data('scope');

    var renderer = pixi.autoDetectRenderer(
            window.innerWidth, window.innerHeight, {
                backgroundColor: 0x333333
            });
    scope.$gameview.get(0).appendChild(renderer.view); // get(0)がないとダメ
    scope.$canvas = $('#gameview canvas')
            .css('position', 'fixed')
            .css('left', '0px')
            .css('top', '0px')
            .css('z-index', '1');

    initEventHandler(); //イベントハンドラ登録

    scope.renderer = renderer;
    scope.stage = new pixi.Container();
    scope.background = stageBackground();

    // 複数形にすると、要素名と一致しなく不便だったので、単数形
    scope.graphics = {
        'company': {},
        'residence': {},
        'lonelyrailnode': {},
        'railedge': {},
        'station': {},
        'stepforhuman': {},
        'icon': {},
        'linestep': {}
    };

    scope.movablegraphics = {
        'train': {},
        'human': {}
    };

    // 画像をロードしたあと、イベントハンドラスプライトを表示
    loadImage(scope.resources);
};

loadImage = function (resources) {
    var scope = $(document).data('scope');
    for (var key in resources) {
        pixi.loader.add(key, resources[key]);
    }
    $('.player').each(function (i, elm) {
        pixi.loader.add('p' + $(elm).attr('id'), $(elm).data('icon'));
        scope.player[$(elm).attr('id')] = $(elm);
    });
    pixi.loader.load(fetchGraphics);
};

initEventHandler = function () {
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
        'mouseleave': onDragEnd,
        'touchend': onDragEnd,
        'mousemove': onDragMove,
        'touchmove': onDragMove,
        'wheel': onWheel
    });

    // Fullscreen in pixi is resizing the renderer to be window.innerWidth by window.innerHeight
    window.addEventListener("resize", handleResize);
};

handleResize = function () {
    var scope = $(document).data('scope');
    scope.renderer.resize(window.innerWidth, window.innerHeight);
    fetchGraphics();
    rewriteTempResource();
};

/**
 * 背景を描画する.
 * @returns {PIXI.Container} container
 */
stageBackground = function () {
    var scope = $(document).data('scope');

    var container = new pixi.Container();

    var scale = $('#scale').text();
    var border = Math.floor(scale / 4) * 4 - 1;

    var diff = {
        x: parseFloat(scope.$centerX.val()) / Math.pow(2, border),
        y: parseFloat(scope.$centerY.val()) / Math.pow(2, border)
    };

    diff.x -= Math.floor(diff.x);
    diff.y -= Math.floor(diff.y);

    var offset = {
        x: scope.renderer.width / 2,
        y: scope.renderer.height / 2
    };

    var range = Math.max(scope.renderer.width, scope.renderer.height);
    var num = Math.pow(2, scale - border);

    [-1, +1].forEach(function (op) {
        // 真ん中が太くならないように
        for (var i = Math.max(op, 0); i < num; i++) {
            var xBar = new pixi.Graphics()
                    .lineStyle(consts.background.width, consts.background.color, consts.background.alpha)
                    .moveTo(0, (op * i - diff.y) * range / num)
                    .lineTo(scope.renderer.width, (op * i - diff.y) * range / num);
            xBar.y = offset.y;
            container.addChild(xBar);

            var yBar = new pixi.Graphics()
                    .lineStyle(consts.background.width, consts.background.color, consts.background.alpha)
                    .moveTo((op * i - diff.x) * range / num, 0)
                    .lineTo((op * i - diff.x) * range / num, scope.renderer.height);
            yBar.x = offset.x;
            container.addChild(yBar);
        }
    });

    scope.stage.addChild(container);

    return container;
};

// fetchGraphics()にすると、ajaxでよびだされない
fetchGraphics = function () {
    var scope = $(document).data('scope');

    scope.stage.removeChild(scope.background);
    scope.background = stageBackground();

    // 既存のリソースにマークをつける
    markOldToGraphics(scope.graphics);

    // 画像つきリソースを作成する。
    for (var name in spriteResources) {
        // クラス名 : .リソース名
        $('.' + name).each(function (i, elm) {
            upsertSprite(name, name, scope.graphics, $(elm).attr('id'), $(elm));
        });
    }

    // 線タイプのリソースを作成する
    for (var name in lineResources) {
        // クラス名 : .リソース名
        $('.' + name).each(function (i, elm) {
            var opts = $(elm).data('ismine') ? lineResources[name].my : lineResources[name].other;
            if (scope.graphics[name][$(elm).attr('id')]) {
                // 更新する
                // Graphicsの移動の仕方が分からなkったので、リライトする
                scope.stage.removeChild(scope.graphics[name][$(elm).attr('id')]);
                scope.graphics[name][$(elm).attr('id')]
                        = stageLine($(elm), opts);
            } else {
                // 新規作成
                scope.graphics[name][$(elm).attr('id')]
                        = stageLine($(elm), opts);
            }
        });
    }

    $('.player').each(function (i, elm) {
        if ($(elm).data('isin')) {
            upsertSprite('icon', 'p' + $(elm).attr('id'), scope.graphics, $(elm).attr('id'), $(elm), true);
        }
    });

    // divタグ中に存在しないリソースを削除
    deleteOldGraphics(scope.graphics);
    scope.renderer.render(scope.stage);
};

markOldToGraphics = function (graphics) {
    // 既存のリソースにマークをつける
    // 更新後、マークが残っていたら削除する。
    for (var name in graphics) {
        for (var key in graphics[name]) {
            graphics[name][key].old = true;
        }
    }
};

deleteOldGraphics = function (graphics) {
    var scope = $(document).data('scope');
    // divタグ中に存在しないリソースを削除
    for (var name in graphics) {
        for (var key in graphics[name]) {
            if (graphics[name][key].old) {
                scope.stage.removeChild(graphics[name][key]);
                delete graphics[name][key];
            }
        }
    }
};

fetchMovableGraphics = function () {
    var scope = $(document).data('scope');
    markOldToGraphics(scope.movablegraphics);

    for (var name in movableSprites) {
        $('.' + name).each(function (i, elm) {
            upsertSprite(name, name, scope.movablegraphics, $(elm).attr('id'), $(elm));
        });
    }

    for (var name in movableCircles) {
        $('.' + name).each(function (i, elm) {
            var pos = toViewPos(
                    parseFloat($(elm).data('x')),
                    parseFloat($(elm).data('y')));
            upsertCircle(name, scope.movablegraphics,
                    $(elm).attr('id'), pos, movableCircles[name])
        });
    }

    deleteOldGraphics(scope.movablegraphics);
    scope.renderer.render(scope.stage);
};

upsertSprite = function (name, restype, graphics, id, $elm, isBringToFront) {
    if (graphics[name][id]) {
        // 更新
        graphics[name][id].old = false;
        updateSprite(graphics[name][id], $elm, isBringToFront);
    } else {
        // 新規作成
        graphics[name][id] = stageResourceSprite(restype, $elm);
    }
};

stageResourceSprite = function (type, $elm) {
    var scope = $(document).data('scope');
    var pos = toViewPos(
            parseFloat($elm.data('x')),
            parseFloat($elm.data('y')));

    var obj = new pixi.Sprite(pixi.loader.resources[type].texture);
    obj.anchor.set(0.5, 0.5);
    obj.alpha = 1;
    obj.position.set(pos.x, pos.y);
    obj.scale.x = 0.5;
    obj.scale.y = 0.5;

    scope.stage.addChild(obj);
    return obj;
};

updateSprite = function (sprite, $elm, isBringToFront) {
    var scope = $(document).data('scope');
    var pos = toViewPos(
            parseFloat($elm.data('x')),
            parseFloat($elm.data('y')));

    sprite.position.set(pos.x, pos.y);

    if (isBringToFront) {
        scope.stage.removeChild(sprite);
        scope.stage.addChild(sprite);
    }
};

/**
 * 路線のlineではなく、線のline
 * @param {type} $elm
 * @param {type} opts
 * @returns line
 */
stageLine = function ($elm, opts) {
    var scope = $(document).data('scope');
    var color = opts.color ? opts.color : scope.player[$elm.data('pid')].data('color').replace('#', '0x');

    var container = new pixi.Container;
    var obj = new pixi.Graphics();

    var from = toViewPos(
            parseFloat($elm.data('from-x')),
            parseFloat($elm.data('from-y')));
    var to = toViewPos(
            parseFloat($elm.data('to-x')),
            parseFloat($elm.data('to-y')));

    var line = slideEdge(from, to, opts.slide);

    obj.lineStyle(opts.scale, color)
            .moveTo(line.fromx, line.fromy)
            .lineTo(line.tox, line.toy);

    obj.alpha = opts.alpha;

    if ($elm.data('idx')) {
        var idx = new pixi.Text($elm.data('idx'), {
            fontSize: 10, fill: 0xffffff
        });
        idx.x = (line.fromx + line.tox) / 2 - 5;
        idx.y = (line.fromy + line.toy) / 2 - 5;
        container.addChild(idx);
    }

    container.addChild(obj);
    scope.stage.addChild(container);
    return container;
};

slideEdge = function (from, to, scale) {
    var theta = Math.atan2(to.y - from.y, to.x - from.x) - Math.PI / 2;
    return {
        fromx: from.x + Math.cos(theta) * scale,
        fromy: from.y + Math.sin(theta) * scale,
        tox: to.x + Math.cos(theta) * scale,
        toy: to.y + Math.sin(theta) * scale
    };
};

/**
 * サーバ上のパスを画面上の座標に変換する
 * @param {type} x
 * @param {type} y
 * @returns {Point} 座標値
 */
toViewPos = function (x, y) {
    var scope = $(document).data('scope');
    return {
        x: (x - scope.$centerX.val())
                * Math.max(scope.renderer.width, scope.renderer.height)
                * Math.pow(2, -$('#scale').text())
                + scope.renderer.width / 2,
        y: (y - scope.$centerY.val())
                * Math.max(scope.renderer.width, scope.renderer.height)
                * Math.pow(2, -$('#scale').text())
                + scope.renderer.height / 2
    };
};
/**
 * スライダーを動かした時、リアルタイムで拡大、縮小できるようにする
 * @param {type} event
 * @param {type} ui
 * @returns {undefined}
 */
handleSlide = function (event, ui) {
    $('#scale').text(ui.value / 100);
    fetchGraphics();
    fetchMovableGraphics();
    rewriteTempResource();
};

onDragStart = function (event) {
    var scope = $(document).data('scope');
    this.moving = false;
    this.dragging = true;
    $('body').css('user-select', 'none'); // body要素上のテキストが選択され、ドラッグできなくなるため
    this.startGamePos = {
        x: parseFloat(scope.$centerX.val()),
        y: parseFloat(scope.$centerY.val())
    };
    this.startPosition = toViewPosFromMouse(event);
};

onDragEnd = function (event) {
    var scope = $(document).data('scope');

    var mousePos = toViewPosFromMouse(event, scope.mousePos);

    if (this.startPosition && this.startPosition.x === mousePos.x
            && this.startPosition.y === mousePos.y) {
        // クリックと判定した
        // 当初 mouse move のイベントの有無でクリックかどうか判定していたが、
        // クリック時にmouse moveイベントが発火(chromeのみ?)のため座標比較する方式に変更
        // マウスの座標をゲーム上の座標に変換する
        var gamePos = toGamePos(scope.cursor ? scope.cursor : mousePos);

        scope.$clickX.val(gamePos.x);
        scope.$clickY.val(gamePos.y);

        if (scope.neighborEdge) {
            registerEdgeId([
                {name: 'railEdge1.id', value: scope.neighborEdge.e1id},
                {name: 'railEdge2.id', value: scope.neighborEdge.e2id}
            ]);
        } else {
            registerClickPos([
                {name: 'gamePos.x', value: gamePos.x},
                {name: 'gamePos.y', value: gamePos.y}]);
        }
    } else {
        // ドラッグと判定した
        // mouseup時のリロードは xhtml側で行う
    }
    this.dragging = false;
    $('body').css('user-select', 'auto');
    this.startGamePos = null;
    this.startPosition = null;
};

/**
 * ドラッグ時、新しくなるcenterX, centerYを求める
 * @param {jQuery.Event} event
 * @returns {nm$_gameview.onDragMove}
 */
onDragMove = function (event) {
    var scope = $(document).data('scope');
    scope.mousePos = toViewPosFromMouse(event, scope.mousePos);

    if (this.dragging) {
        var newCenterPos = toNewCenterGamePos(
                this.startGamePos, this.startPosition, scope.mousePos);

        scope.$centerX.val(newCenterPos.x);
        scope.$centerY.val(newCenterPos.y);

        fetchGraphics();
        fetchMovableGraphics();

        if (scope.tailNode) {
            var pos = toViewPos(scope.tailNode.gamex, scope.tailNode.gamey);
            scope.tailNode.x = pos.x;
            scope.tailNode.y = pos.y;
        }
    }

    rewriteTempResource();
};

onWheel = function (event) {
    var sliderscale = Math.round($('#sliderscale').val());

    if (event.originalEvent.deltaY > 0) {
        sliderscale = Math.min(consts.slider.max * 100, sliderscale + 40);
    }
    if (event.originalEvent.deltaY < 0) {
        sliderscale = Math.max(consts.slider.min * 100, sliderscale - 40);
    }

    $('#sliderscale').val(sliderscale);
    $('#scale').text(sliderscale / 100);
    PF('slider').setValue(sliderscale);
    fetchGraphics();
    fetchMovableGraphics();
    rewriteTempResource();

    registerScale([{name: 'scale', value: sliderscale / 100}]);
};

onPollStart = function () {
    var scope = $(document).data('scope');
    scope.isPolling = true;
};

onPollEnd = function () {
    var scope = $(document).data('scope');
    scope.isPolling = false;
};

onAjaxStart = function () {
    var scope = $(document).data('scope');
    if (scope.isPolling) {
        $('#ajaxstatus').hide();
    } else {
        $('#ajaxstatus').show();
    }
};

/**
 * イベント変数からマウス座標を取得する。
 * @param {jQuery.Event} event
 * @param {Point} def マウス座標値が取得できなかったとき返す値。指定なしのときは{x: 0, y: 0}を返す。
 * @returns {Point} マウス座標値
 */
toViewPosFromMouse = function (event, def) {
    if (def === undefined) {
        def = {x: 0, y: 0};
    }

    if (event.offsetX && event.offsetY) {
        return {x: event.offsetX, y: event.offsetY};
    } else if (event.originalEvent.touches && event.originalEvent.touches[0]) {
        return {x: event.originalEvent.touches[0].pageX, y: event.originalEvent.touches[0].pageY};
    }
    return def;
};

toNewCenterGamePos = function (startGamePos, startViewPos, newViewPos) {
    var scope = $(document).data('scope');
    return {
        x: startGamePos.x
                - (newViewPos.x - startViewPos.x)
                * Math.pow(2, $('#scale').text()) / Math.max(scope.renderer.width, scope.renderer.height),
        y: startGamePos.y
                - (newViewPos.y - startViewPos.y)
                * Math.pow(2, $('#scale').text()) / Math.max(scope.renderer.width, scope.renderer.height)
    };
};

toGamePos = function (pos) {
    var scope = $(document).data('scope');
    // - 0.5 するのは画面の中央がcenterXに対応するため
    return {
        x: (pos.x / Math.max(scope.renderer.width, scope.renderer.height)
                - 0.5 * scope.renderer.width / Math.max(scope.renderer.width, scope.renderer.height))
                * Math.pow(2, $('#scale').text())
                + parseFloat(scope.$centerX.val()),
        y: (pos.y / Math.max(scope.renderer.width, scope.renderer.height)
                - 0.5 * scope.renderer.height / Math.max(scope.renderer.width, scope.renderer.height))
                * Math.pow(2, $('#scale').text())
                + parseFloat(scope.$centerY.val())
    };
};

fireClickMenu = function () {
    var scope = $(document).data('scope');
    if (scope.tailNode && scope.extendEdge && scope.cursor) {
        extendRail();
    } else {
        $('#openclickmenu').click();
    }
};

startExtendingMode = function (x, y) {
    var scope = $(document).data('scope');
    scope.tailNode = stageTempCircle(toViewPos(x, y), tempResources.tailNode);
    scope.tailNode.gamex = x;
    scope.tailNode.gamey = y;
    scope.cursor = stageTempCircle(scope.mousePos, tempResources.cursor);
    scope.extendEdge = stageTempLine(scope.tailNode, scope.cursor, tempResources.extendEdge);
    scope.renderer.render(scope.stage);
};

rewriteTempResource = function () {
    var scope = $(document).data('scope');
    var neighbor;

    removeTempResourceNeighbor();

    if (scope.tailNode) {
        scope.stage.removeChild(scope.tailNode);
        var vpos = toViewPos(scope.tailNode.gamex, scope.tailNode.gamey);
        scope.tailNode.x = vpos.x;
        scope.tailNode.y = vpos.y;
        // ゲーム座標を引き継ぐため一時保管
        var gamex = scope.tailNode.gamex;
        var gamey = scope.tailNode.gamey;
        scope.tailNode = stageTempCircle(scope.tailNode, tempResources.tailNode);
        scope.tailNode.gamex = gamex;
        scope.tailNode.gamey = gamey;
        var $n = findNeighbor('railnode', scope.mousePos);
        if ($n) {
            var npos = toViewPos(parseFloat($n.data('x')), parseFloat($n.data('y')));
            if (npos.x !== scope.tailNode.x && npos.y !== scope.tailNode.y) {
                neighbor = npos;
            }
        }
    } else {
        writeTempResourceNeighbor();
    }

    if (scope.cursor) {
        scope.stage.removeChild(scope.cursor);
        scope.cursor = stageTempCircle(neighbor ? neighbor : scope.mousePos, tempResources.cursor);
    }

    if (scope.extendEdge) {
        scope.stage.removeChild(scope.extendEdge);
        scope.extendEdge = stageTempLine(scope.tailNode, scope.cursor, tempResources.extendEdge);
    }

    scope.renderer.render(scope.stage);
};

removeTempResourceNeighbor = function () {
    var scope = $(document).data('scope');
    if (scope.neighborNode) {
        scope.stage.removeChild(scope.neighborNode);
        scope.neighborNode = null;
    }
    if (scope.neighborEdge) {
        scope.stage.removeChild(scope.neighborEdge.e1);
        scope.stage.removeChild(scope.neighborEdge.e2);
        scope.neighborEdge = null;
    }
};

writeTempResourceNeighbor = function () {
    var scope = $(document).data('scope');

    var $nnode = findNeighbor('railnode', scope.mousePos);

    if ($nnode) {
        var nnodepos = toViewPos(parseFloat($nnode.data('x')), parseFloat($nnode.data('y')));
        scope.neighborNode = stageTempCircle(nnodepos, tempResources.neighborNode);
    } else {
        var nedges = findNeighborEdge('railedge', scope.mousePos);

        if (nedges) {
            scope.neighborEdge = {
                e1id: nedges.$e1.attr('id').replace('railedge', ''),
                e2id: nedges.$e2.attr('id').replace('railedge', ''),
                e1: stageLine(nedges.$e1, tempResources.neighborEdge),
                e2: stageLine(nedges.$e2, tempResources.neighborEdge)
            };
        }
    }
};

upsertCircle = function (name, graphics, id, pos, opts) {
    if (graphics[name][id]) {
        // 更新
        graphics[name][id].old = false;
        graphics[name][id].x = pos.x;
        graphics[name][id].y = pos.y;
    } else {
        // 新規作成
        graphics[name][id] = stageTempCircle(pos, opts);
    }
};

stageTempCircle = function (pos, opts) {
    var scope = $(document).data('scope');
    var obj = new pixi.Graphics();
    obj.x = pos.x;
    obj.y = pos.y;
    obj.alpha = opts.alpha;
    obj.beginFill(opts.color);
    obj.drawCircle(0, 0, opts.radius);
    obj.endFill();
    scope.stage.addChild(obj);
    return obj;
};

stageTempLine = function (head, tail, opts) {
    var scope = $(document).data('scope');
    var obj = new pixi.Graphics();

    obj.alpha = opts.alpha;
    obj.lineStyle(opts.width, opts.color)
            .moveTo(head.x, head.y)
            .lineTo(tail.x, tail.y);
    scope.stage.addChild(obj);
    return obj;
};

nextExtendingMode = function (x, y) {
    var scope = $(document).data('scope');
    deleteTempGraphics();

    fetchGraphics();
    startExtendingMode(x, y);
};

finishOperation = function () {
    deleteTempGraphics();
};

deleteTempGraphics = function () {
    var scope = $(document).data('scope');

    if (scope.tailNode) {
        scope.stage.removeChild(scope.tailNode);
        scope.tailNode = null;
    }
    if (scope.cursor) {
        scope.stage.removeChild(scope.cursor);
        scope.cursor = null;
    }
    if (scope.extendEdge) {
        scope.stage.removeChild(scope.extendEdge);
        scope.extendEdge = null;
    }
};

findNeighbor = function (name, pos) {
    var neighbor = null;
    var minDist = Number.MAX_VALUE;
    $('.' + name).each(function (i, elm) {
        var otherPos = toViewPos(
                parseFloat($(elm).data('x')),
                parseFloat($(elm).data('y')));

        var dist = (otherPos.x - pos.x) * (otherPos.x - pos.x) + (otherPos.y - pos.y) * (otherPos.y - pos.y);

        if (dist < minDist && dist < consts.round * consts.round) {
            neighbor = $(elm);
        }
    });

    return neighbor;
};

findNeighborEdge = function (name, pos, isDirect) {
    var $nearest = null;
    var minDist = Number.MAX_VALUE;
    $('.' + name).filter(function () {
        return $(this).data('ismine');
    }).each(function (i, elm) {
        var from = toViewPos(
                parseFloat($(elm).data('from-x')),
                parseFloat($(elm).data('from-y')));
        var to = toViewPos(
                parseFloat($(elm).data('to-x')),
                parseFloat($(elm).data('to-y')));
        var ev = {
            'x': to.x - from.x,
            'y': to.y - from.y
        };
        var u = {
            'x': ev.x / dist(0, 0, ev.x, ev.y),
            'y': ev.y / dist(0, 0, ev.x, ev.y)
        };
        var pv = {
            'x': pos.x - from.x,
            'y': pos.y - from.y
        };
        // 外積
        var d = Math.abs(ev.x * pv.y - ev.y * pv.x);
        var l = d / dist(0, 0, ev.x, ev.y);
        if (l < consts.round) {
            // 垂線の足がes[i]上にあるか
            var xlen = pv.x * u.x + pv.y * u.y;
            if (xlen <= 0 || xlen >= dist(0, 0, ev.x, ev.y)) {
                return;
            }
            if (l < minDist) {
                minDist = l;
                $nearest = $(elm);
            }
        }
    });
    if ($nearest === null) {
        return null;
    }
    return isDirect ? $nearest : {
        '$e1': $nearest,
        '$e2': $('#' + $nearest.data('reverseid'))
    };
};

dist = function (x1, y1, x2, y2) {
    var dx = x2 - x1;
    var dy = y2 - y1;
    return Math.sqrt(dx * dx + dy * dy);
};

handleCompleteRemoving = function () {
    removeTempResourceNeighbor();
    fetchGraphics();
};