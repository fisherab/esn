'use strict';

// Based on yeoman generated file. Use of htmlmin and imagemin has been removed and the 
// image matching for usemin has been improved.

module.exports = function(grunt) {

	// Load grunt tasks automatically
	require('load-grunt-tasks')(grunt);

	// Time how long tasks take. Can help when optimizing build times
	require('time-grunt')(grunt);

	// Configurable paths for the application
	var appConfig = {
		app : require('./bower.json').appPath || 'app',
		dist : 'dist'
	};

	// Define the configuration for all the tasks
	grunt
			.initConfig({

				// Project settings
				yeoman : appConfig,

				// Watches files for changes and runs tasks based on the changed
				// files
				watch : {
					bower : {
						files : [ 'bower.json' ],
						tasks : [ 'wiredep' ]
					},
					js : {
						files : [ '<%= yeoman.app %>/scripts/{,*/}*.js' ],
						tasks : [ 'newer:jshint:all' ],
						options : {
							livereload : '<%= connect.options.livereload %>'
						}
					},
					jsTest : {
						files : [ 'test/spec/{,*/}*.js' ],
						tasks : [ 'newer:jshint:test', 'karma' ]
					},
					styles : {
						files : [ '<%= yeoman.app %>/styles/{,*/}*.css' ],
						tasks : [ 'newer:copy:styles', 'autoprefixer' ]
					},
					gruntfile : {
						files : [ 'Gruntfile.js' ]
					},
					livereload : {
						options : {
							livereload : '<%= connect.options.livereload %>'
						},
						files : [ '<%= yeoman.app %>/{,*/}*.html',
								'.tmp/styles/{,*/}*.css',
								'<%= yeoman.app %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}' ]
					}
				},

				// The actual grunt server settings
				connect : {
					options : {
						port : 9000,
						// Change this to '0.0.0.0' to access the server from
						// outside.
						hostname : 'localhost',
						livereload : 35729
					},
					proxies : [ {
						context : '/esn', // the context of the data service
						host : 'localhost', // wherever the data service is
						// running
						port : 8080
					// the port that the data service is running on
					} ],
					livereload : {
						options : {
							open : true,
							middleware : function(connect) {
								return [
										require('grunt-connect-proxy/lib/utils').proxyRequest,
										connect.static('.tmp'),
										connect()
												.use(
														'/bower_components',
														connect
																.static('./bower_components')),
										connect.static(appConfig.app) ];
							}
						}
					},
					test : {
						options : {
							port : 9001,
							middleware : function(connect) {
								return [
										connect.static('.tmp'),
										connect.static('test'),
										connect()
												.use(
														'/bower_components',
														connect
																.static('./bower_components')),
										connect.static(appConfig.app) ];
							}
						}
					},
					dist : {
						options : {
							open : true,
							base : '<%= yeoman.dist %>'
						}
					}
				},

				// Make sure code styles are up to par and there are no obvious
				// mistakes
				jshint : {
					options : {
						jshintrc : '.jshintrc',
						reporter : require('jshint-stylish')
					},
					all : {
						src : [ 'Gruntfile.js',
								'<%= yeoman.app %>/scripts/{,*/}*.js' ]
					},
					test : {
						options : {
							jshintrc : 'test/.jshintrc'
						},
						src : [ 'test/spec/{,*/}*.js' ]
					}
				},

				// Empties folders to start fresh
				clean : {
					dist : {
						files : [ {
							dot : true,
							src : [ '.tmp', '<%= yeoman.dist %>/{,*/}*',
									'!<%= yeoman.dist %>/.git*' ]
						} ]
					},
					server : '.tmp'
				},

				// Add vendor prefixed styles
				autoprefixer : {
					options : {
						browsers : [ 'last 1 version' ]
					},
					dist : {
						files : [ {
							expand : true,
							cwd : '.tmp/styles/',
							src : '{,*/}*.css',
							dest : '.tmp/styles/'
						} ]
					}
				},

				// Automatically inject Bower components into the app
				wiredep : {
					app : {
						src : [ '<%= yeoman.app %>/index.html' ],
						ignorePath : /\.\.\//
					}
				},

				// Renames files for browser caching purposes
				filerev : {
					dist : {
						src : [
								'<%= yeoman.dist %>/scripts/{,*/}*.js',
								'<%= yeoman.dist %>/styles/{,*/}*.css',
								'<%= yeoman.dist %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}',
								'<%= yeoman.dist %>/styles/fonts/*' ]
					}
				},

				// Reads HTML for usemin blocks to enable smart builds that
				// automatically
				// concat, minify and revision files. Creates configurations in
				// memory so
				// additional tasks can operate on them
				useminPrepare : {
					html : '<%= yeoman.app %>/index.html',
					options : {
						dest : '<%= yeoman.dist %>',
						flow : {
							html : {
								steps : {
									js : [ 'concat', 'uglifyjs' ],
									css : [ 'cssmin' ]
								},
								post : {}
							}
						}
					}
				},

				// Performs rewrites based on filerev and the useminPrepare
				// configuration - with extra patterns to fix the ng-src problem
				// by looking for any reference to image files in the images
				// directory.
				usemin : {
					html : [ '<%= yeoman.dist %>/{,*/}*.html' ],
					css : [ '<%= yeoman.dist %>/styles/{,*/}*.css' ],
					options : {
						assetsDirs : [ '<%= yeoman.dist %>',
								'<%= yeoman.dist %>/images' ],
						patterns : {
							html : [
									[
											/(images\/.*?\.(?:gif|jpeg|jpg|png|webp|svg))/gm,
											'Update the angular directives that ref revved images ' ],
									[ /<script.+src=['"]([^"']+)["']/gm,
											'Update the HTML to reference our concat/min/revved script files' ],
									[ /<link[^\>]+href=['"]([^"']+)["']/gm,
											'Update the HTML with the new css filenames' ],
									[
											/<img[^\>]*[^\>\S]+src=['"]([^'"\)#]+)(#.+)?["']/gm,
											'Update the HTML with the new img filenames' ],
									[ /<video[^\>]+src=['"]([^"']+)["']/gm,
											'Update the HTML with the new video filenames' ],
									[ /<video[^\>]+poster=['"]([^"']+)["']/gm,
											'Update the HTML with the new poster filenames' ],
									[ /<source[^\>]+src=['"]([^"']+)["']/gm,
											'Update the HTML with the new source filenames' ],
									[
											/data-main\s*=['"]([^"']+)['"]/gm,
											'Update the HTML with data-main tags',
											function(m) {
												return m.match(/\.js$/) ? m : m	+ '.js';
											}, function(m) {
												return m.replace('.js', '');
											} ],
									[ /data-(?!main).[^=]+=['"]([^'"]+)['"]/gm,
											'Update the HTML with data-* tags' ],
									[ /url\(\s*['"]?([^"'\)]+)["']?\s*\)/gm,
											'Update the HTML with background imgs, case there is some inline style' ],
									[ /<a[^\>]+href=['"]([^"']+)["']/gm,
											'Update the HTML with anchors images' ],
									[ /<input[^\>]+src=['"]([^"']+)["']/gm,
											'Update the HTML with reference in input' ],
									[ /<meta[^\>]+content=['"]([^"']+)["']/gm,
											'Update the HTML with the new img filenames in meta tags' ],
									[ /<object[^\>]+data=['"]([^"']+)["']/gm,
											'Update the HTML with the new object filenames' ],
									[
											/<image[^\>]*[^\>\S]+xlink:href=['"]([^"'#]+)(#.+)?["']/gm,
											'Update the HTML with the new image filenames for svg xlink:href links' ],
									[
											/<image[^\>]*[^\>\S]+src=['"]([^'"\)#]+)(#.+)?["']/gm,
											'Update the HTML with the new image filenames for src links' ],
									[
											/<(?:img|source)[^\>]*[^\>\S]+srcset=['"]([^"'\s]+)(?:\s\d[mx])["']/gm,
											'Update the HTML with the new image filenames for srcset links' ],
									[
											/<(?:use|image)[^\>]*[^\>\S]+xlink:href=['"]([^'"\)#]+)(#.+)?["']/gm,
											'Update the HTML within the <use> tag when referencing an external url with svg sprites as in svg4everybody' ]

							]
						}
					}
				},

				svgmin : {
					dist : {
						files : [ {
							expand : true,
							cwd : '<%= yeoman.app %>/images',
							src : '{,*/}*.svg',
							dest : '<%= yeoman.dist %>/images'
						} ]
					}
				},

				// ng-annotate tries to make the code safe for minification
				// automatically
				// by using the Angular long form for dependency injection.
				ngAnnotate : {
					dist : {
						files : [ {
							expand : true,
							cwd : '.tmp/concat/scripts',
							src : [ '*.js', '!oldieshim.js' ],
							dest : '.tmp/concat/scripts'
						} ]
					}
				},

				// Replace Google CDN references
				cdnify : {
					dist : {
						html : [ '<%= yeoman.dist %>/*.html' ]
					}
				},

				// Copies remaining files to places other tasks can use
				copy : {
					dist : {
						files : [
								{
									expand : true,
									dot : true,
									cwd : '<%= yeoman.app %>',
									dest : '<%= yeoman.dist %>',
									src : [ '*.{ico,png,txt}', '.htaccess',
											'*.html', 'views/{,*/}*.html',
											'images/{,*/}*.{webp}', 'fonts/*' ]
								}, {
									expand : true,
									cwd : '.tmp/images',
									dest : '<%= yeoman.dist %>/images',
									src : [ 'generated/*' ]
								}, {
									expand : true,
									cwd : 'bower_components/bootstrap/dist',
									src : 'fonts/*',
									dest : '<%= yeoman.dist %>'
								},  {
									expand : true,
									cwd : '<%= yeoman.app %>/images',
									src : '{,*/}*.{png,jpg,jpeg,gif}',
									dest : '<%= yeoman.dist %>/images'
								} ]
					},
					styles : {
						expand : true,
						cwd : '<%= yeoman.app %>/styles',
						dest : '.tmp/styles/',
						src : '{,*/}*.css'
					}
				},

				// Run some tasks in parallel to speed up the build process
				concurrent : {
					server : [ 'copy:styles' ],
					test : [ 'copy:styles' ],
					dist : [ 'copy:styles', 'svgmin' ]
				},

				// Test settings
				karma : {
					unit : {
						configFile : 'test/karma.conf.js',
						singleRun : true
					}
				}
			});

	grunt.registerTask('serve', 'Compile then start a connect web server',
			function(target) {
				if (target === 'dist') {
					return grunt.task
							.run([ 'build', 'connect:dist:keepalive' ]);
				}

				grunt.task.run([ 'clean:server', 'wiredep',
						'configureProxies:server', 'concurrent:server',
						'autoprefixer', 'connect:livereload', 'watch' ]);
			});

	grunt.registerTask('test', [ 'clean:server', 'concurrent:test',
			'autoprefixer', 'connect:test', 'karma' ]);

	grunt.registerTask('build', [ 'clean:dist', 'wiredep', 'useminPrepare',
			'concurrent:dist', 'autoprefixer', 'concat', 'ngAnnotate',
			'copy:dist', 'cdnify', 'cssmin', 'uglify', 'filerev', 'usemin' ]);

	grunt.registerTask('default', [ 'newer:jshint', 'test', 'build' ]);
};
