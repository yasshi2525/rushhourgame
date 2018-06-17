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
var buffer = require('vinyl-buffer');
var uglify = require('gulp-uglify');
var rename = require('gulp-rename');
var jsdoc = require("gulp-jsdoc3");

gulp.task('watch', function () {
    gulp.watch('./src/main/webapp/resources/js/*.js', ['buildNormal']);
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

gulp.task('doc', function (cb) {
    var config = require('./jsdoc.json');
    gulp.src(['README.md', './src/main/webapp/resources/js/*.js'], {read: false})
            .pipe(jsdoc(config, cb));
});

function build(minimize) {
    _build('index.js', ['./src/main/webapp/resources/js/index.js'], minimize);
    _build('admin.js', ['./src/main/webapp/resources/js/admin.js'], minimize);
}

function _build(target, entries, minimize) {
    browserify({entries: entries})
            .bundle()
            .pipe(source(target))
            .pipe(buffer())
            .pipe(uglify({compress: minimize, mangle: minimize}))
            .pipe(rename(target))
            .pipe(gulp.dest('./src/main/webapp/resources/js/build'));
}
