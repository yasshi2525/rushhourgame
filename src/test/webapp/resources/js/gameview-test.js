/* 
 * The MIT License
 *
 * Copyright 2017 yasshi2525 (https://twitter.com/yasshi2525).
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

/* global expect, spyOn, pixi */

describe('test gameview', function () {
    var origin = {x: 0, y: 0};

    var lefttopViewPos = {x: 0, y: 0};
    var centerViewPos = {x: 250, y: 250};
    var rightbottomViewPos = {x: 500, y: 500};
    var doNothing = function () {};
    var scope = null;
    var $mockElm = {
        data: function () {
            return '128';
        }
    };
    var emptyTexture = {texture: ''};
    var mockSprite = {
        position: {
            x: -10,
            y: -11,
            set: function (newX, newY) {
                this.x = newX;
                this.y = newY;
            },
            reset: function () {
                this.x = -10;
                this.y = -11;
            }
        }
    };

    loadImage({
        company: 'base/src/main/webapp/resources/image/s_company.png',
        residence: 'base/src/main/webapp/resources/image/s_residence.png',
        station: 'base/src/main/webapp/resources/image/s_station.png',
        train: 'base/src/main/webapp/resources/image/s_train.png'
    });

    beforeEach(function () {
        // ここでjQuery objectを初期化しないと動かない
        $('body').append("<div id='scale'/>");
        $('body').append("<div id='centerX'/>");
        $('body').append("<div id='centerY'/>");
        $('body').append("<div id='clickX'/>");
        $('body').append("<div id='clickY'/>");
        $('body').append("<div id='gameview'/>");
        scope = {
            renderer: {
                width: 500,
                height: 500,
                render: doNothing
            },
            $centerX: $('#centerX'),
            $centerY: $('#centerY'),
            $clickX: $('#clickX'),
            $clickY: $('#clickY'),
            $gameview: $('#gameview'),
            $canvas: $('#gameview canvas'),
            stage: {
                addChild: doNothing,
                removeChild: doNothing
            },
            graphics: {
                'company': {},
                'residence': {},
                'railedge': {},
                'station': {},
                'stepforhuman': {}
            }
        };
        $(document).data('scope', scope);
        $('#scale').text(8);
        $('#centerX').val('0');
        $('#centerY').val('0');
        window.moving = false;
        window.dragging = false;
        window.startGamePos = null;
        window.startPosition = null;
        window.registerClickPos = doNothing;
        window.extendRail = doNothing;
        mockSprite.position.reset();
    });

    // initはexportの中にあるためテストできない。

    describe('test initEventHandler', function () {
        it('invoke', function () {
            initEventHandler();
        });
    });

    describe('test fetchGraphicx', function () {
        beforeEach(function () {
            spyOn(window, 'updateSprite').and.callFake(doNothing);
            spyOn(window, 'stageResourceSprite').and.returnValue({});
            spyOn(window, 'stageLine').and.returnValue({});
            spyOn(scope.stage, 'removeChild').and.callThrough();
        });

        it('do nothing when empty', function () {
            fetchGraphics();
            expect(window.updateSprite.calls.any()).toEqual(false);
            expect(window.stageResourceSprite.calls.any()).toEqual(false);
            expect(scope.stage.removeChild.calls.any()).toEqual(false);
            expect(window.stageLine.calls.any()).toEqual(false);
        });

        ['company', 'residence', 'station'].forEach(function (type) {
            it('create sprite ' + type + ' when DOM exists', function () {
                $('body').append("<div class='" + type + "' id = 'no1'/>");

                fetchGraphics();

                expect(window.updateSprite.calls.any()).toEqual(false);
                expect(window.stageResourceSprite.calls.count()).toEqual(1);
                expect(window.stageLine.calls.any()).toEqual(false);
                expect(scope.stage.removeChild.calls.any()).toEqual(false);
                expect(scope.graphics[type].no1).toBeDefined();

            });

            it('remain sprite ' + type + ' when DOM exists', function () {
                scope.graphics[type].no1 = {};
                $('body').append("<div class='" + type + "' id = 'no1'/>");

                fetchGraphics();

                expect(window.updateSprite.calls.count()).toEqual(1);
                expect(window.stageResourceSprite.calls.any()).toEqual(false);
                expect(window.stageLine.calls.any()).toEqual(false);
                expect(scope.stage.removeChild.calls.any()).toEqual(false);
                expect(scope.graphics[type].no1).toBeDefined();
            });

            it('remove sprite ' + type + ' when DOM doesn\'t exist', function () {
                scope.graphics[type].no1 = {};

                fetchGraphics();

                expect(window.updateSprite.calls.any()).toEqual(false);
                expect(window.stageResourceSprite.calls.any()).toEqual(false);
                expect(window.stageLine.calls.any()).toEqual(false);
                expect(scope.stage.removeChild.calls.count()).toEqual(1);
                expect(scope.graphics[type].no1).not.toBeDefined();
            });

            afterEach(function () {
                $('#no1.' + type).remove();
            });
        });

        ['railedge'].forEach(function (type) {
            it('create line ' + type + ' when DOM exists', function () {
                $('body').append("<div class='" + type + "' id = 'no1'/>");

                fetchGraphics();

                expect(window.updateSprite.calls.any()).toEqual(false);
                expect(window.stageResourceSprite.calls.any()).toEqual(false);
                expect(window.stageLine.calls.count()).toEqual(1);
                expect(scope.stage.removeChild.calls.any()).toEqual(false);
                expect(scope.graphics[type].no1).toBeDefined();
            });

            it('recreate line ' + type + ' when DOM exists', function () {
                scope.graphics[type].no1 = {};
                $('body').append("<div class='" + type + "' id = 'no1'/>");

                fetchGraphics();

                expect(window.updateSprite.calls.any()).toEqual(false);
                expect(window.stageResourceSprite.calls.any()).toEqual(false);
                expect(window.stageLine.calls.count()).toEqual(1);
                expect(scope.stage.removeChild.calls.count()).toEqual(1);
                expect(scope.graphics[type].no1).toBeDefined();
            });

            it('remove line ' + type + ' when DOM exists', function () {
                scope.graphics[type].no1 = {};

                fetchGraphics();

                expect(window.updateSprite.calls.any()).toEqual(false);
                expect(window.stageResourceSprite.calls.any()).toEqual(false);
                expect(window.stageLine.calls.any()).toEqual(false);
                expect(scope.stage.removeChild.calls.count()).toEqual(1);
                expect(scope.graphics[type].no1).not.toBeDefined();
            });

            afterEach(function () {
                $('#no1.' + type).remove();
            });
        });
    });

    describe('test stageResourceSprite', function () {
        it('create sprite', function () {
            expect(stageResourceSprite('company', $mockElm))
                    .not.toBeNull();
        });
    });

    describe('test updateSprite', function () {
        it('change pos', function () {
            updateSprite(mockSprite, $mockElm);
            expect(mockSprite.position.x).toEqual(500);
            expect(mockSprite.position.y).toEqual(500);
        });
    });

    describe('test stageLine', function () {
        it('create line', function () {
            expect(stageLine($mockElm, {scale: 1, color: 0xaaaaaa}))
                    .not.toBeNull();
        });
    });

    describe('test slideEdge', function () {
        it('return not null', function () {
            // 計算が難しいので呼ぶだけ
            expect(slideEdge(origin, origin, 8)).not.toBeNull();
        });
    });

    describe('test toViewPos', function () {

        it('game (0, 0) is view center', function () {
            expect(toViewPos(0, 0)).toEqual(centerViewPos);
        });

        it('game (-128, -128) is view left-top', function () {
            expect(toViewPos(-128, -128)).toEqual(lefttopViewPos);
        });

        it('game (128, 128) is view right-bottom', function () {
            expect(toViewPos(128, 128)).toEqual(rightbottomViewPos);
        });

        it('game (128, 64)) is view right-bottom (when (500, 250) window)', function () {
            scope.renderer.width = 500;
            scope.renderer.height = 250;
            expect(toViewPos(128, 64)).toEqual({x: 500, y: 250});
        });

        it('game (64, 128)) is view right-bottom (when (250, 500) window)', function () {
            scope.renderer.width = 250;
            scope.renderer.heigh = 500;
            expect(toViewPos(64, 128)).toEqual({x: 250, y: 500});
        });

        afterEach(function () {
            scope.renderer.width = 500;
            scope.renderer.heigh = 500;
        });
    });

    describe('test handleSlide', function () {
        beforeEach(function () {
            spyOn(window, 'fetchGraphics').and.callFake(doNothing);
        });

        it('set 1000 -> 10', function () {
            handleSlide(null, {value: 1000});
            expect($('#scale').text()).toEqual('10');
        });
    });

    describe('test onDragStart', function () {
        it('prepare variables', function () {
            scope.$centerX.val('4');
            scope.$centerY.val('3');
            spyOn(window, 'toViewPosFromMouse').and.returnValue({x: 11, y: 12});
            onDragStart();
            expect(window.moving).toEqual(false);
            expect(window.dragging).toEqual(true);
            expect(window.startGamePos).toEqual({x: 4, y: 3});
        });
    });

    describe('test onDragEnd', function () {
        beforeEach(function () {
            spyOn(window, 'toViewPosFromMouse').and.returnValue({x: 11, y: 12});
            spyOn(window, 'toGamePos').and.returnValue({x: 11, y: 12});
            spyOn(window, 'registerClickPos').and.callFake(doNothing);
        });

        it('reload when clicked', function () {
            window.startPosition = {x: 11, y: 12};
            onDragEnd();
            expect(scope.$clickX.val()).toEqual('11');
            expect(scope.$clickY.val()).toEqual('12');
            expect(window.registerClickPos.calls.any()).toEqual(true);
            expect(window.dragging).toEqual(false);
            expect(window.startGamePos).toBeNull();
            expect(window.startPosition).toBeNull();
        });
        it('do nothing just when drag ended', function () {
            window.startPosition = {x: 0, y: 0};
            onDragEnd();
            expect(window.registerClickPos.calls.any()).toEqual(false);
            expect(window.dragging).toEqual(false);
            expect(window.startGamePos).toBeNull();
            expect(window.startPosition).toBeNull();
        });
    });

    describe('test onDragMove', function () {
        beforeEach(function () {
            spyOn(window, 'fetchGraphics').and.callFake(doNothing);
            spyOn(window, 'toNewCenterGamePos').and.returnValue({x: 11, y: 12});
            spyOn(window, 'toViewPosFromMouse').and.callFake(doNothing);
            spyOn(window, 'rewriteTempResource').and.callFake(doNothing);
        });

        it('do nothing just when moving', function () {
            window.dragging = false;
            onDragMove();
            expect(window.fetchGraphics.calls.any()).toEqual(false);
        });

        it('change center when dragged', function () {
            window.dragging = true;
            window.startGamePos = origin;
            window.startPosition = centerViewPos;

            onDragMove({});

            expect(window.fetchGraphics.calls.any()).toEqual(true);
            expect(scope.$centerX.val()).toBe('11');
            expect(scope.$centerY.val()).toBe('12');
        });

        it('change tailNode pos when dragged', function () {
            window.dragging = true;
            window.startGamePos = origin;
            window.startPosition = centerViewPos;
            scope.tailNode = {x: 100, y: 100, gamex: 100, gamey: 100};

            onDragMove({});

            expect(scope.tailNode.x).not.toEqual(100);
            expect(scope.tailNode.y).not.toEqual(100);
        });
    });

    describe('test toViewPosFromMouse', function () {
        it('touch enabled', function () {
            expect(toViewPosFromMouse({
                originalEvent: {touches: [{offsetX: 10, offsetY: 20}]}
            })).toEqual({x: 10, y: 20});
        });

        it('mouse enabled', function () {
            expect(toViewPosFromMouse(
                    {offsetX: 10, offsetY: 20}
            )).toEqual({x: 10, y: 20});
        });
    });

    describe('test toNewCenterGamePos', function () {
        it('move (0, 0) -> (256, 256) when dragged left-top to right-bottom', function () {
            expect(toNewCenterGamePos(
                    origin,
                    lefttopViewPos,
                    rightbottomViewPos
                    )).toEqual({x: -256, y: -256});
        });
        it('move (0, 0) -> (128, 128) when dragged center to left-top', function () {
            expect(toNewCenterGamePos(
                    origin,
                    centerViewPos,
                    lefttopViewPos
                    )).toEqual({x: 128, y: 128});
        });
        it('move (128, 128) -> (0, 0) when dragged center to right-bottom', function () {
            expect(toNewCenterGamePos(
                    {x: 128, y: 128},
                    centerViewPos,
                    rightbottomViewPos
                    )).toEqual(origin);
        });
        it('move (0, 0) -> (128, 64) when dragged center to left-top, window (500, 250)', function () {
            scope.renderer.width = 500;
            scope.renderer.height = 250;
            expect(toNewCenterGamePos(
                    origin,
                    {x: 250, y: 125},
                    lefttopViewPos
                    )).toEqual({x: 128, y: 64});
        });
        it('move (0, 0) -> (64, 128) when dragged center to left-top, window (250, 500)', function () {
            scope.renderer.width = 250;
            scope.renderer.height = 500;
            expect(toNewCenterGamePos(
                    origin,
                    {x: 125, y: 250},
                    lefttopViewPos
                    )).toEqual({x: 64, y: 128});
        });

        afterEach(function () {
            scope.renderer.width = 500;
            scope.renderer.height = 500;
        });
    });

    describe('test toGamePos', function () {
        it('view center is game (0, 0)', function () {
            expect(toGamePos(centerViewPos))
                    .toEqual(origin);
        });
        it('view left-top is game (-128, -128)', function () {
            expect(toGamePos(lefttopViewPos))
                    .toEqual({x: -128, y: -128});
        });
        it('view right-bottom is (128, 128)', function () {
            expect(toGamePos(rightbottomViewPos))
                    .toEqual({x: 128, y: 128});
        });
        it('view right-bottom is (128, 64) when window (500, 250)', function () {
            scope.renderer.width = 500;
            scope.renderer.height = 250;
            expect(toGamePos({x: 500, y: 250}))
                    .toEqual({x: 128, y: 64});
        });
        it('view right-bottom is (64, 128) when window (250, 500)', function () {
            scope.renderer.width = 250;
            scope.renderer.height = 500;
            expect(toGamePos({x: 250, y: 500}))
                    .toEqual({x: 64, y: 128});
        });

        afterEach(function () {
            scope.renderer.width = 500;
            scope.renderer.height = 500;
        });
    });

    describe('test fireClickMenu', function () {
        beforeEach(function () {
            spyOn(window, 'extendRail').and.callFake(doNothing);
        });

        it('test fire click menu', function () {
            fireClickMenu();
            expect(window.extendRail.calls.any()).toEqual(false);
        });

        it('test fire click menu', function () {
            scope.tailNode = {};
            scope.cursor = {};
            scope.extendEdge = {};
            fireClickMenu();
            expect(window.extendRail.calls.any()).toEqual(true);
        });
    });

    describe('test startExtendingMode', function () {
        it('test invoke', function () {
            scope.mousePos = centerViewPos;
            startExtendingMode(lefttopViewPos.x, lefttopViewPos.y);
        });

        it('test rewrite', function () {
            scope.mousePos = centerViewPos;
            startExtendingMode(lefttopViewPos.x, lefttopViewPos.y);
            startExtendingMode(lefttopViewPos.x, lefttopViewPos.y);
        });
    });

    describe('test nextExtendingMode', function () {
        beforeEach(function () {
            spyOn(window, 'startExtendingMode').and.callFake(doNothing);
            spyOn(window, 'fetchGraphics').and.callFake(doNothing);
        });

        it('test invoke', function () {
            scope.tailNode = {};
            scope.cursor = {};
            scope.extendEdge = {};

            nextExtendingMode(0, 0);

            expect(scope.tailNode).toBeNull();
            expect(scope.cursor).toBeNull();
            expect(scope.extendEdge).toBeNull();
        });
    });

    describe('test finishesOperation', function () {
        it('test invoke', function () {
            finishOperation();
        });
    });

    describe('test rewriteTempResource', function () {
        beforeEach(function () {
            spyOn(window, 'stageTempCircle').and.returnValue({});
            spyOn(window, 'stageTempLine').and.callFake(doNothing);
        });

        it('test do nothing when no object', function () {
            rewriteTempResource();
            expect(window.stageTempCircle.calls.any()).toEqual(false);
            expect(window.stageTempLine.calls.any()).toEqual(false);
        });

        it('test rewrite', function () {
            scope.tailNode = {gamex: 100, gamey: 100};
            scope.cursor = {};
            scope.extendEdge = {};
            rewriteTempResource();
            expect(window.stageTempCircle.calls.count()).toEqual(2);
            expect(window.stageTempLine.calls.count()).toEqual(1);
        });
    });

    describe('test findNeighbor', function () {
        it('test find neighbor', function () {
            $('body').append("<div class='railnode' id = 'no1' data-x='0.0' data-y='0.0'/>");

            expect(findNeighbor('railnode', {x: 240, y: 240})).not.toBeNull();
            expect(findNeighbor('railnode', centerViewPos)).not.toBeNull();
            expect(findNeighbor('unexists', centerViewPos)).toBeNull();
            expect(findNeighbor('railnode', origin)).toBeNull();
        });

        it('test find nearest', function () {
            $('body').append("<div class='railnode' id = 'no1' data-x='1.0' data-y='1.0'/>");
            $('body').append("<div class='railnode' id = 'no2' data-x='0.0' data-y='0.0'/>");

            expect(findNeighbor('railnode', centerViewPos).attr('id')).toEqual('no2');
        });
    });

    afterEach(function () {
        $('#scale').remove();
        $('#centerX').remove();
        $('#centerY').remove();
        $('#clickX').remove();
        $('#clickY').remove();
        $('#gameview').remove();
    });
});

// test gameview でいろいろmockを作っているので、別に分けた
describe('test initPixi', function () {
    it('invoke', function () {
        initPixi();
        // exports.init は見つからないといわれてできなかった。
    });
});