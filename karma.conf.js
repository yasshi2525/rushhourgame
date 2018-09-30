// Karma configuration
// Generated on Sun Dec 03 2017 15:45:50 GMT+0900 (東京 (標準時))

module.exports = function (config) {
    config.set({

        // base path that will be used to resolve all patterns (eg. files, exclude)
        basePath: '',

        // frameworks to use
        // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
        frameworks: ['browserify', 'jasmine'],

        // list of files / patterns to load in the browser
        files: [
             {
                 pattern: 'src/main/webapp/resources/image/*.png', 
                 watched: false, included: false, served: true, nocache: false
             },
            'https://code.jquery.com/jquery-3.2.1.min.js',
            'src/main/webapp/resources/js/gameview.js',
            // index と gameviewをよぶと毎回初期化されてエラー
            'src/test/webapp/resources/js/*.js'
        ],

        // list of files to exclude
        exclude: [
        ],

        // preprocess matching files before serving them to the browser
        // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
        preprocessors: {
            'src/main/webapp/resources/js/*.js': ['browserify']
        },

        browserify: {
            debug: true,
            transform: [
                require('browserify-istanbul')({
                })
            ]
        },

        coverageReporter: {
            dir: 'target/jscoverage',
            reporters: [
                { type: 'html', subdir: 'html' },
                { type: 'cobertura', subdir: 'cobertura', file: 'coverage.xml' },
                { type: 'lcov', subdir: 'lcov'}
            ]
        },

        // test results reporter to use
        // possible values: 'dots', 'progress'
        // available reporters: https://npmjs.org/browse/keyword/karma-reporter
        reporters: ['mocha', 'coverage', 'coveralls'],

        // web server port
        port: 9876,

        // enable / disable colors in the output (reporters and logs)
        colors: true,

        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_INFO,

        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: true,

        // start these browsers
        // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
        browsers: ['ChromeHeadless'],

        // Continuous Integration mode
        // if true, Karma captures browsers, runs the tests and exits
        singleRun: true,

        // Concurrency level
        // how many browser should be started simultaneous
        concurrency: Infinity,
        
        // for docker
        flags: ['--no-sandbox']
    });
};
