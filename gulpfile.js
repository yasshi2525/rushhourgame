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

var gulp = require('gulp');
var karma = require('karma');
var browserify = require('browserify');
var source = require('vinyl-source-stream');
var streamify = require('gulp-streamify');
var uglify = require('gulp-uglify');
var rename = require('gulp-rename');

gulp.task('watch', function () {
    gulp.watch('./src/main/webapp/resources/js/*.js', ['buildNormal', 'karma']);
});

gulp.task('buildNormal', function () {
    build(false);
});

gulp.task('default', function () {
    build(true);
});

gulp.task('karma', function (done) {
    var karmaServer = new karma.Server({
        configFile: __dirname + '/karma.conf.js',
        singleRun: true
    }, function (exitCode) {
        done();
        process.exit(exitCode);
    }).start();
});

function build(minimize) {
    _build('index.js', ['./src/main/webapp/resources/js/index.js'], minimize);
    _build('admin.js', ['./src/main/webapp/resources/js/admin.js'], minimize);
}

function _build(target, entries, minimize) {
    var b = browserify({entries: entries})
            .bundle()
            .pipe(source(target));
    if (minimize) {
        b.pipe(streamify(uglify()));
    }
    b.pipe(rename(target))
            .pipe(gulp.dest('./src/main/webapp/resources/js/build'));
}
