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
            background: {},
            graphics: {
                'company': {},
                'residence': {},
                'railedge': {},
                'station': {},
                'stepforhuman': {}
            },
            movablegraphics: {
                'train': {},
                'human': {}
            },
            player: {}
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
        window.registerEdgeId = doNothing;
        window.extendRail = doNothing;
        mockSprite.position.reset();
    });

    // initはexportの中にあるためテストできない。

    describe('test initEventHandler', function () {
        it('invoke', function () {
            initEventHandler();
        });
    });

    describe('test handleResize', function () {
        it('invoke', function () {
            scope.renderer.resize = doNothing;
            spyOn(window, 'fetchGraphics').and.callFake(doNothing);
            spyOn(window, 'rewriteTempResource').and.callFake(doNothing);
            handleResize();
        });
    });

    describe('test stageBackground', function () {
        it('invoke', function () {
            stageBackground();
        });
    });

    describe('test fetchGraphicx', function () {
        beforeEach(function () {
            spyOn(window, 'stageBackground').and.returnValue({});
            spyOn(window, 'updateSprite').and.callFake(doNothing);
            spyOn(window, 'stageResourceSprite').and.returnValue({});
            spyOn(window, 'stageLine').and.returnValue({});
            spyOn(scope.stage, 'removeChild').and.callThrough();
        });

        it('do nothing when empty', function () {
            $('body').append("<div class='player' id='admin' data-isin='true'/>");
            $('body').append("<div class='player' id='other' data-isin='false'/>");
            spyOn(window, 'upsertSprite').and.callFake(doNothing);

            fetchGraphics();
            expect(window.updateSprite.calls.any()).toEqual(false);
            expect(window.stageResourceSprite.calls.any()).toEqual(false);
            expect(scope.stage.removeChild.calls.count()).toEqual(1);
            expect(window.stageLine.calls.any()).toEqual(false);

            $('#admin').remove();
            $('#other').remove();
        });

        ['company', 'residence', 'station'].forEach(function (type) {
            it('create sprite ' + type + ' when DOM exists', function () {
                $('body').append("<div class='" + type + "' id = 'no1'/>");

                fetchGraphics();

                expect(window.updateSprite.calls.any()).toEqual(false);
                expect(window.stageResourceSprite.calls.count()).toEqual(1);
                expect(window.stageLine.calls.any()).toEqual(false);
                expect(scope.stage.removeChild.calls.count()).toEqual(1);
                expect(scope.graphics[type].no1).toBeDefined();

            });

            it('remain sprite ' + type + ' when DOM exists', function () {
                scope.graphics[type].no1 = {};
                $('body').append("<div class='" + type + "' id = 'no1'/>");

                fetchGraphics();

                expect(window.updateSprite.calls.count()).toEqual(1);
                expect(window.stageResourceSprite.calls.any()).toEqual(false);
                expect(window.stageLine.calls.any()).toEqual(false);
                expect(scope.stage.removeChild.calls.count()).toEqual(1);
                expect(scope.graphics[type].no1).toBeDefined();
            });

            it('remove sprite ' + type + ' when DOM doesn\'t exist', function () {
                scope.graphics[type].no1 = {};

                fetchGraphics();

                expect(window.updateSprite.calls.any()).toEqual(false);
                expect(window.stageResourceSprite.calls.any()).toEqual(false);
                expect(window.stageLine.calls.any()).toEqual(false);
                expect(scope.stage.removeChild.calls.count()).toEqual(2);
                expect(scope.graphics[type].no1).not.toBeDefined();
            });

            afterEach(function () {
                $('#no1.' + type).remove();
            });
        });

        ['railedge'].forEach(function (type) {
            it('create line ' + type + ' when DOM exists', function () {
                $('body').append("<div class='" + type + "' id='no1' data-ismine='true'/>");

                fetchGraphics();

                expect(window.updateSprite.calls.any()).toEqual(false);
                expect(window.stageResourceSprite.calls.any()).toEqual(false);
                expect(window.stageLine.calls.count()).toEqual(1);
                expect(scope.stage.removeChild.calls.count()).toEqual(1);
                expect(scope.graphics[type].no1).toBeDefined();
            });

            it('recreate line ' + type + ' when DOM exists', function () {
                scope.graphics[type].no1 = {};
                $('body').append("<div class='" + type + "' id = 'no1'/>");

                fetchGraphics();

                expect(window.updateSprite.calls.any()).toEqual(false);
                expect(window.stageResourceSprite.calls.any()).toEqual(false);
                expect(window.stageLine.calls.count()).toEqual(1);
                expect(scope.stage.removeChild.calls.count()).toEqual(2);
                expect(scope.graphics[type].no1).toBeDefined();
            });

            it('remove line ' + type + ' when DOM exists', function () {
                scope.graphics[type].no1 = {};

                fetchGraphics();

                expect(window.updateSprite.calls.any()).toEqual(false);
                expect(window.stageResourceSprite.calls.any()).toEqual(false);
                expect(window.stageLine.calls.any()).toEqual(false);
                expect(scope.stage.removeChild.calls.count()).toEqual(2);
                expect(scope.graphics[type].no1).not.toBeDefined();
            });

            afterEach(function () {
                $('#no1.' + type).remove();
            });
        });
    });

    describe('test fetchMovableGraphics', function () {
        it('test invoke', function () {
            $('body').append("<div class='train' id = 'no1'/>");
            $('body').append("<div class='human' id = 'no2'/>");
            fetchMovableGraphics();
            fetchMovableGraphics();
            $('#no1.train').remove();
            $('#no2.human').remove();
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

        it('change pos with isBringToFront', function () {
            updateSprite(mockSprite, $mockElm, true);
        });
    });

    describe('test stageLine', function () {
        it('create line', function () {
            $('body').append("<div class='player' id = '128' data-color='#FFFFFF'/>");
            scope.player['128'] = $('#128');
            expect(stageLine($mockElm, {my: {scale: 1, color: 0xaaaaaa}}))
                    .not.toBeNull();

            $('#128').remove();
        });

        it('create colored line with label', function () {
            $('body').append("<div class='player' id = '128' data-color='#FFFFFF'/>");
            expect(stageLine($mockElm, {color: 0xaaaaaa, slide: 5, scale: 5, alpha: 0.5}))
                    .not.toBeNull();
            $('#128').remove();
        });

        it('create line without label', function () {
            $('body').append("<div class='player' id = 'edge1' data-color='#FFFFFF'/>");
            expect(stageLine($('#edge1'), {color: 0xaaaaaa, slide: 5, scale: 5, alpha: 0.5}))
                    .not.toBeNull();
            $('#edge1').remove();
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
            spyOn(window, 'registerClickPos').and.callFake(doNothing);
            spyOn(window, 'registerEdgeId').and.callFake(doNothing);
        });

        it('send click pos when clicked', function () {
            spyOn(window, 'toGamePos').and.returnValue({x: 11, y: 12});
            window.startPosition = {x: 11, y: 12};
            onDragEnd();
            expect(scope.$clickX.val()).toEqual('11');
            expect(scope.$clickY.val()).toEqual('12');
            expect(window.registerClickPos.calls.any()).toEqual(true);
            expect(window.dragging).toEqual(false);
            expect(window.startGamePos).toBeNull();
            expect(window.startPosition).toBeNull();
        });

        it('send cursor pos when clicked', function () {
            window.startPosition = {x: 11, y: 12};
            scope.cursor = {x: 0, y: 0};
            onDragEnd();
            expect(scope.$clickX.val()).toEqual('-128');
            expect(scope.$clickY.val()).toEqual('-128');
            expect(window.registerEdgeId.calls.any()).toEqual(false);
            expect(window.registerClickPos.calls.any()).toEqual(true);
        });

        it('send edge id when clicked', function () {
            window.startPosition = {x: 11, y: 12};
            scope.neighborEdge = {e1id: 1, e2id: 2};
            onDragEnd();
            expect(window.registerEdgeId.calls.any()).toEqual(true);
            expect(window.registerClickPos.calls.any()).toEqual(false);
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
                originalEvent: {touches: [{pageX: 10, pageY: 20}]}
            })).toEqual({x: 10, y: 20});
        });

        it('mouse enabled', function () {
            expect(toViewPosFromMouse(
                    {offsetX: 10, offsetY: 20}
            )).toEqual({x: 10, y: 20});
        });

        it('both disabled', function () {
            expect(toViewPosFromMouse({originalEvent: {}}
            )).toEqual({x: 0, y: 0});
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
            spyOn(window, 'stageTempLine').and.callFake(doNothing);
        });

        it('test do nothing when no object', function () {
            spyOn(window, 'stageTempCircle').and.returnValue({});
            rewriteTempResource();
            expect(window.stageTempCircle.calls.any()).toEqual(false);
            expect(window.stageTempLine.calls.any()).toEqual(false);
        });

        it('test rewrite', function () {
            spyOn(window, 'stageTempCircle').and.returnValue({});
            scope.tailNode = {gamex: 100, gamey: 100};
            scope.cursor = {};
            scope.extendEdge = {};
            rewriteTempResource();
            expect(window.stageTempCircle.calls.count()).toEqual(2);
            expect(window.stageTempLine.calls.count()).toEqual(1);
        });

        it('test rewrite when found neighbor', function () {
            spyOn(window, 'stageTempCircle').and.callThrough();
            scope.tailNode = {x: 0, y: 0, gamex: -128, gamey: -128};
            scope.cursor = {};
            scope.extendEdge = {};
            scope.mousePos = {x: 245, y: 245};
            $('body').append("<div class='railnode' id = 'no1' data-x='0.0' data-y='0.0'/>");

            rewriteTempResource();

            expect(scope.cursor.x).toEqual(250);
            expect(scope.cursor.y).toEqual(250);
        });

        it('test rewrite when mouse surrounding tailNode', function () {
            spyOn(window, 'stageTempCircle').and.callThrough();
            scope.tailNode = {x: 0, y: 0, gamex: -128, gamey: -128};
            scope.cursor = {};
            scope.extendEdge = {};
            scope.mousePos = {x: 5, y: 5};
            $('body').append("<div class='railnode' id = 'no1' data-x='-128.0' data-y='-128.0'/>");

            rewriteTempResource();

            expect(scope.cursor.x).toEqual(5);
            expect(scope.cursor.y).toEqual(5);
        });
        afterEach(function () {
            $('#no1').remove();
        });
    });

    describe('test removeTempResoureNeighbor', function () {
        it('test remove', function () {
            scope.neighborNode = {};
            scope.neighborEdge = {};

            removeTempResourceNeighbor();

            expect(scope.neighborNode).toBeNull();
            expect(scope.neighborEdge).toBeNull();
        });
    });

    describe('test writeTempResourceNeighbor ', function () {
        beforeEach(function () {
            $('body').append("<div class='railnode' id = 'no0' data-x='0.0' data-y='0.0'/>");

            $('body').append("<div class='railedge' id='no1'/>");
            $('#no1').data('ismine', true);
            $('#no1').data('from-x', '0');
            $('#no1').data('from-y', '0');
            $('#no1').data('to-x', '64');
            $('#no1').data('to-y', '0');
            $('#no1').data('reverseid', 'no2');

            $('body').append("<div class='railedge' id='no2'/>");
            $('#no2').data('ismine', true);
            $('#no2').data('from-x', '64');
            $('#no2').data('from-y', '0');
            $('#no2').data('to-x', '0');
            $('#no2').data('to-y', '0');
            $('#no2').data('reverseid', 'no1');

            spyOn(window, 'stageTempCircle').and.returnValue({});
            spyOn(window, 'stageLine').and.returnValue({});
        });

        it('test write node', function () {
            scope.mousePos = {x: 250, y: 250};
            scope.neighborEdge = null;

            writeTempResourceNeighbor();

            expect(scope.neighborNode).not.toBeNull();
            expect(scope.neighborEdge).toBeNull();
        });

        it('test write edge', function () {
            scope.mousePos = {x: 300, y: 250};
            scope.neighborNode = null;

            writeTempResourceNeighbor();

            expect(scope.neighborNode).toBeNull();
            expect(scope.neighborEdge.e1).toEqual({});
            expect(scope.neighborEdge.e2).toEqual({});
        });

        afterEach(function () {
            $('#no0').remove();
            $('#no1').remove();
            $('#no2').remove();
        });
    });

    describe('test writeTempResourceNeighbor ', function () {
        it('test write node', function () {
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

        afterEach(function () {
            $('#no1').remove();
            $('#no2').remove();
        });
    });

    describe('test findNeighborEdge', function () {
        beforeEach(function () {
            $('body').append("<div class='railedge' id='no1'/>");
            $('#no1').data('ismine', true);
            $('#no1').data('from-x', '0');
            $('#no1').data('from-y', '0');
            $('#no1').data('to-x', '64');
            $('#no1').data('to-y', '0');
            $('#no1').data('reverseid', 'no2');

            $('body').append("<div class='railedge' id='no2'/>");
            $('#no2').data('ismine', true);
            $('#no2').data('from-x', '64');
            $('#no2').data('from-y', '0');
            $('#no2').data('to-x', '0');
            $('#no2').data('to-y', '0');
            $('#no2').data('reverseid', 'no1');

        });

        it('test do nothing when only other player edge', function () {
            $('#no1').data('ismine', false);
            $('#no2').data('ismine', false);
            expect(findNeighborEdge('railedge')).toBeNull();
        });

        it('test far point', function () {
            expect(findNeighborEdge('railedge', {x: 250, y: 280})).toBeNull();
        });

        it('test out of line', function () {
            expect(findNeighborEdge('railedge', {x: 500, y: 250})).toBeNull();
        });

        it('test on line', function () {
            var res = findNeighborEdge('railedge', {x: 300, y: 260});
            expect(res).not.toBeNull();
            expect(res.$e1.attr('id')).toEqual('no1');
            expect(res.$e2.attr('id')).toEqual('no2');
        });

        it('test on line direct mode', function () {
            var res = findNeighborEdge('railedge', {x: 300, y: 260}, true);
            expect(res).not.toBeNull();
            expect(res.attr('id')).toEqual('no1');
        });

        afterEach(function () {
            $('#no1').remove();
            $('#no2').remove();
        });
    });

    describe('test handleCompleteRemoving', function () {
        it('test invoke', function () {
            spyOn(window, 'fetchGraphics').and.callFake(doNothing);
            spyOn(window, 'removeTempResourceNeighbor').and.callFake(doNothing);

            handleCompleteRemoving();
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
        //loadImageを2回実行するとResource読み込みエラーになる
        spyOn(window, 'loadImage').and.callFake(function () {});
        spyOn(window, 'stageBackground').and.returnValue({});
        $('body').append("<div class='player' id='admin' data-icon='base/src/main/webapp/resources/image/s_player.png'/>");
        $('body').append("<div id='gameview'/>");

        var localScope = {
            $gameview: $('#gameview'),
            player: {}
        };
        $(document).data('scope', localScope);

        initPixi();
        // exports.init は見つからないといわれてできなかった。

        $('#admin').remove();
        $('#gameview').remove();
    });
});