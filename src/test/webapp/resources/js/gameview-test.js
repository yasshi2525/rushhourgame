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

describe('test gameview', function () {
    beforeEach(function () {
        $('body').append("<div id='scale'/>");
        $('body').append("<div id='centerX'/>");
        $('body').append("<div id='centerY'/>");
        $('#scale').text(8);
        $('#centerX').val('0');
        $('#centerY').val('0');

        $(document).data('scope', {
            renderer: {
                width: 500,
                height: 500
            },
            $centerX: $('#centerX'),
            $centerY: $('#centerY')
        });
    });

    describe('test onDragMove', function () {
        it('stay', function () {
            this.drargging = true;
            
        });
    });
    describe('test toGamePos', function () {
        it('(center)->(0,0)', function () {
            expect(toGamePos(250, 250))
                    .toEqual({x: 0, y: 0});
        });
    });
});