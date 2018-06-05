package org.geogebra.web.html5;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.DoubleUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window.Location;

public class Browser {
	private static boolean webWorkerSupported = false;
	private static boolean float64supported = true;
	private static Boolean webglSupported = null;

	public static native boolean isFirefox() /*-{
		// copying checking code from the checkWorkerSupport method
		// however, this is not necessarily the best method to decide
		if ($wnd.navigator.userAgent.toLowerCase().indexOf("firefox") != -1) {
			return true;
		}
		return false;
	}-*/;

	/**
	 * Check if browser is Internet Explorer
	 * 
	 * (Note: only IE11 is supported now)
	 * 
	 * @return true if IE
	 */
	public static native boolean isIE() /*-{
		// check if app is running in IE5 or greater
		// clipboardData object is available from IE5 and onwards
		var userAgent = $wnd.navigator.userAgent.toLowerCase();
		if ((userAgent.indexOf('msie ') > -1)
				|| (userAgent.indexOf('trident/') > -1)) {
			return true;
		}
		return false;
	}-*/;

	/**
	 * Check if browser is Safari on iOS
	 * 
	 * (Note: returns true for Chrome on iOS as that's really an iOS Webview)
	 * 
	 * @return true if iOS
	 */
	public static native boolean isiOS() /*-{
		var userAgent = $wnd.navigator.userAgent;

		return userAgent.match(/iPad/i) || userAgent.match(/iPhone/i);

	}-*/;

	/**
	 * https://github.com/cheton/is-electron/blob/master/index.js MIT
	 * 
	 * @return true if running in Electron
	 */
	public static native boolean isElectron() /*-{
		// Renderer process
		if (typeof $wnd !== 'undefined' && typeof $wnd.process === 'object'
				&& $wnd.process.type === 'renderer') {
			return true;
		}

		// Main process
		if (typeof $wnd.process !== 'undefined'
				&& typeof $wnd.process.versions === 'object'
				&& !!$wndprocess.versions.electron) {
			return true;
		}

		// Detect the user agent when the `nodeIntegration` option is set to true
		if (typeof $wnd.navigator === 'object'
				&& typeof $wnd.navigator.userAgent === 'string'
				&& $wnd.navigator.userAgent.indexOf('Electron') >= 0) {
			return true;
		}

		return false;
	}-*/;

	public native static boolean externalCAS() /*-{
		return typeof $wnd.evalGeoGebraCASExternal == 'function'
				&& $wnd.evalGeoGebraCASExternal("1+1") == "2";
	}-*/;

	/**
	 * @param workerpath
	 *            JS folder with workers
	 * @return whether workers are supported
	 */
	public static boolean checkWorkerSupport(String workerpath) {
		if ("tablet".equals(GWT.getModuleName())
				|| "tabletWin".equals(GWT.getModuleName())) {
			return false;
		}
		return nativeCheckWorkerSupport(workerpath);
	}

	private static native boolean nativeCheckWorkerSupport(
			String workerpath) /*-{
		// Worker support in Firefox is incompatible at the moment for zip.js,
		// see http://gildas-lormeau.github.com/zip.js/ for details:
		if (navigator.userAgent.toLowerCase().indexOf("firefox") != -1) {
			@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("INIT: worker not supported in Firefox, fallback for simple js");
			return false;
		}
		if (navigator.userAgent.toLowerCase().indexOf("safari") != -1
			&& navigator.userAgent.toLowerCase().indexOf("chrome") == -1) {
			@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("INIT: worker not supported in Safari, fallback for simple js");
			return false;
		}
		
	    try {
	    	var worker = new $wnd.Worker(workerpath+"js/workercheck.js");
	    } catch (e) {
	    	@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("INIT: worker not supported (no worker at " + workerpath + "), fallback for simple js");
	    	
	    	return false;
	    }
	    @org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("INIT: workers are supported");
	    	
	    worker.terminate();
	    return true;
	}-*/;

	public static native boolean checkIfFallbackSetExplicitlyInArrayBufferJs() /*-{
		if ($wnd.zip && $wnd.zip.useWebWorkers === false) {
			//we set this explicitly in arraybuffer.js
			@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("INIT: workers maybe supported, but fallback set explicitly in arraybuffer.js");
			return true;
		}
		return false;
	}-*/;

	/**
	 * @return whether we are running under iOS
	 */
	public static native String getMobileOperatingSystem()/*-{
		var userAgent = $wnd.navigator.userAgent;

		//iOS detection from: http://stackoverflow.com/a/9039885/177710
		if (/Mac|iPad|iPhone|iPod/.test(userAgent) && !$wnd.MSStream) {
			return "iOS";
		}
		return "unknown";
	}-*/;

	/**
	 * Checks whether browser supports float64. Must be called before a polyfill
	 * kicks in.
	 */
	public static void checkFloat64() {
		float64supported = doCheckFloat64();
	}

	public static boolean isFloat64supported() {
		return float64supported;
	}

	private static native boolean doCheckFloat64()/*-{
		var floatSupport = 'undefined' !== typeof Float64Array;
		return 'undefined' !== typeof Float64Array;
	}-*/;

	/**
	 * 
	 * @return true if WebAssembly supported
	 */
	public static native boolean webAssemblySupported()/*-{

		// currently iOS11 giac.wasm gives slightly wrong results
		// eg Numeric(fractionalPart(2.7)) gives 0.6999999999999 rather than 0.7
		var iOS = /iPad|iPhone|iPod/.test($wnd.navigator.userAgent)
				&& !$wnd.MSStream;

		return !iOS && !!$wnd.WebAssembly;
	}-*/;

	public static native boolean supportsPointerEvents(boolean usePen)/*-{
		//$wnd.console.log("PEN SUPPORT" + usePen + "," + (!!$wnd.PointerEvent));
		if (usePen && $wnd.PointerEvent) {
			return true;
		}
		return $wnd.navigator.msPointerEnabled ? true : false;
	}-*/;

	private static native boolean isHTTP() /*-{
		return $wnd.location.protocol != 'file:';
	}-*/;

	public static boolean supportsSessionStorage() {
		return !Browser.isIE() || Browser.isHTTP();
	}

	/**
	 * @param thumb
	 *            original URL
	 * @return URL using appropriate protocol (data or https)
	 */
	public static String normalizeURL(String thumb) {
		if (thumb.startsWith("data:")) {
			return thumb;
		}
		String url;
		if (thumb.startsWith("http://") || thumb.startsWith("file://")) {
			url = thumb.substring("http://".length());
		} else if (thumb.startsWith("https://")) {
			url = thumb.substring("https://".length());
		} else if (thumb.startsWith("//")) {
			url = thumb.substring("//".length());
		} else {
			url = thumb;
		}

		return "https://" + url;
	}

	/**
	 * @return whether WebGL is supported
	 */
	public static boolean supportsWebGL() {
		if (webglSupported == null) {
			webglSupported = supportsWebGLNative();
		}
		return webglSupported.booleanValue();
	}

	public static void mockWebGL() {
		webglSupported = true;
	}

	/**
	 * Native check for WebGL support based on
	 * http://stackoverflow.com/questions/11871077/proper-way-to-detect-webgl-
	 * support
	 */
	public static native boolean supportsWebGLNative()/*-{
		try {
			var canvas = $doc.createElement('canvas');
			var ret = !!$wnd.WebGLRenderingContext
					&& (canvas.getContext('webgl') || canvas
							.getContext('experimental-webgl'));
			return !!ret;
		} catch (e) {
			return false;
		}
	}-*/;

	public static native boolean supportsWebGLTriangleFan()/*-{
		return $wnd.WebGLRenderingContext
				&& (!!$wnd.WebGLRenderingContext.TRIANGLE_FAN);
	}-*/;

	/**
	 * @return whether we are running this from another website (local install
	 *         of app bundle)
	 */
	public static boolean runningLocal() {
		return Location.getProtocol().startsWith("http")
				&& Location.getHost() != null
				&& !Location.getHost().contains("geogebra.org");
	}

	public native static String navigatorLanguage() /*-{
		return $wnd.navigator.language || "en";
	}-*/;

	public static native boolean isAndroidVersionLessThan(double d) /*-{
		var navString = $wnd.navigator.userAgent.toLowerCase();
		if (navString.indexOf("android") < 0) {
			return false;
		}
		if (parseFloat(navString.substring(navString.indexOf("android") + 8)) < d) {
			return true;
		}
		return false;

	}-*/;

	/**
	 * @param parent
	 *            element to be scaled
	 * @param externalScale
	 *            scale
	 * @param x
	 *            origin x-coord in %
	 * @param y
	 *            origin y-coord in %
	 */
	public static void scale(Element parent, double externalScale, int x, int y) {
		if (externalScale < 0 || parent == null) {
			return;
		}

		String transform = "scale(" + externalScale + "," + externalScale + ")";

		if (DoubleUtil.isEqual(externalScale, 1)) {
			transform = "none";
		}
		String pos = x + "% " + y + "%";

		Style style = parent.getStyle();
		if (style != null) {
			style.setProperty("webkitTransform", transform);
			style.setProperty("mozTransform", transform);
			style.setProperty("msTransform", transform);
			style.setProperty("transform", transform);
			style.setProperty("msTransformOrigin", pos);
			style.setProperty("mozTransformOrigin", pos);
			style.setProperty("webkitTransformOrigin", pos);
			style.setProperty("transformOrigin", pos);
		}
	}

	/**
	 * @return whether webcam input is supported in the browser
	 */
	public static native boolean supportsWebcam() /*-{
		if ($wnd.navigator.getUserMedia || $wnd.navigator.webkitGetUserMedia
				|| $wnd.navigator.mozGetUserMedia
				|| $wnd.navigator.msGetUserMedia) {
			return true;
		}
		return false;
	}-*/;

	/**
	 * @return true if Javascript CAS is supported.
	 */
	public static boolean supportsJsCas() {
		return Browser.isFloat64supported()
				&& !Browser.isAndroidVersionLessThan(4.0);
	}

	public static native boolean isMobile()/*-{
		return !!(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i
				.test($wnd.navigator.userAgent));
	}-*/;

	/**
	 * @return CSS pixel ratio
	 */
	public static native double getPixelRatio() /*-{
		var testCanvas = $doc.createElement("canvas"), testCtx = testCanvas
				.getContext("2d");
		devicePixelRatio = $wnd.devicePixelRatio || 1;
		backingStorePixelRatio = testCtx.webkitBackingStorePixelRatio
				|| testCtx.mozBackingStorePixelRatio
				|| testCtx.msBackingStorePixelRatio
				|| testCtx.oBackingStorePixelRatio
				|| testCtx.backingStorePixelRatio || 1;
		return devicePixelRatio / backingStorePixelRatio;
	}-*/;

	public static native String encodeSVG(String svg) /*-{
		// can't use data:image/svg+xml;utf8 in IE11 / Edge
		// so encode as Base64
		return @org.geogebra.common.util.StringUtil::svgMarker
				+ btoa(unescape(encodeURIComponent(svg)));
	}-*/;

	public static native void exportImage(String url, String title) /*-{
		//idea from http://stackoverflow.com/questions/16245767/creating-a-blob-from-a-base64-string-in-javascript/16245768#16245768

		// no downloading on iOS so just open image/file in new tab
		if (@org.geogebra.web.html5.Browser::isiOS()()) {
			@org.geogebra.web.html5.Browser::openWindow(Ljava/lang/String;)(url);
			return;
		}

		var extension;
		var header;

		// IE11 doesn't have String.startsWith()
		var startsWith = function(data, input) {
			return data.substring(0, input.length) === input;
		}
		//global function in Chrome Kiosk App
		if (typeof $wnd.ggbExportFile == "function") {
			$wnd.ggbExportFile(url, title);
			return;
		}

		var base64encoded = true;

		if (startsWith(url, @org.geogebra.common.util.StringUtil::pngMarker)) {
			extension = "image/png";
			header = @org.geogebra.common.util.StringUtil::pngMarker;
		} else if (startsWith(url,
				@org.geogebra.common.util.StringUtil::svgMarker)) {
			extension = "image/svg+xml";
			header = @org.geogebra.common.util.StringUtil::svgMarker;
		} else if (startsWith(url,
				@org.geogebra.common.util.StringUtil::gifMarker)) {
			extension = "image/gif";
			header = @org.geogebra.common.util.StringUtil::gifMarker;
		} else if (startsWith(url,
				@org.geogebra.common.util.StringUtil::pdfMarker)) {
			extension = "application/pdf";
			header = @org.geogebra.common.util.StringUtil::pdfMarker;
		} else if (startsWith(url,
				@org.geogebra.common.util.StringUtil::txtMarker)) {
			extension = "text/plain";
			header = @org.geogebra.common.util.StringUtil::txtMarker;
			base64encoded = false;
		} else if (startsWith(url,
				@org.geogebra.common.util.StringUtil::htmlMarker)) {
			extension = "text/html";
			header = @org.geogebra.common.util.StringUtil::htmlMarker;
			base64encoded = false;
		} else {
			$wnd.console.log("unknown extension " + url.substring(0, 30));
			return;
		}

		// $wnd.android is set for Android, iOS, Win8
		// Yes, really!
		if ($wnd.android) {
			$wnd.android.share(url, title, extension);
			return;
		}

		// Chrome limits to 2Mb so use Blob
		// https://stackoverflow.com/questions/695151/data-protocol-url-size-limitations/41755526#41755526
		// https://stackoverflow.com/questions/38781968/problems-downloading-big-filemax-15-mb-on-google-chrome/38845151#38845151

		// msSaveBlob: IE11, Edge
		if ($wnd.navigator.msSaveBlob
				|| $wnd.navigator.userAgent.toLowerCase().indexOf("chrome") > -1) {
			var sliceSize = 512;

			var byteCharacters = url.substring(header.length);

			if (base64encoded) {
				byteCharacters = $wnd.atob(byteCharacters);
			}

			var byteArrays = [];

			for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
				var slice = byteCharacters.slice(offset, offset + sliceSize);

				var byteNumbers = new Array(slice.length);
				for (var i = 0; i < slice.length; i++) {
					byteNumbers[i] = slice.charCodeAt(i);
				}

				var byteArray = new Uint8Array(byteNumbers);

				byteArrays.push(byteArray);
			}

			var blob = new Blob(byteArrays, {
				type : extension
			});

			if ($wnd.navigator.msSaveBlob) {
				// IE11, Edge
				$wnd.navigator.msSaveBlob(blob, title);
			} else {
				// Chrome
				var url2 = $wnd.URL.createObjectURL(blob);
				var a = $doc.createElement("a");
				a.download = title;
				a.href = url2;

				a.onclick = function() {
					requestAnimationFrame(function() {
						$wnd.URL.revokeObjectURL(url2);
					})
				};

				$wnd.setTimeout(function() {
					a.click()
				}, 10);
			}
		} else {

			// Firefox, Safari
			var a = $doc.createElement("a");
			$doc.body.appendChild(a);
			a.style = "display: none";
			a.href = url;
			a.download = title;
			$wnd.setTimeout(function() {
				a.click()
			}, 10);

		}

	}-*/;

	/**
	 * Change URL if we are running on geogebra.org
	 * 
	 * @param string
	 *            new URL
	 */
	public static void changeUrl(String string) {
		if ((Location.getHost() != null
				&& Location.getHost().contains("geogebra.org")
				&& !Location.getHost().contains("autotest"))
				|| string.startsWith("#") || string.startsWith("?")) {
			nativeChangeUrl(string);
		}
	}

	public static native void changeMetaTitle(String title) /*-{
		$wnd.changeMetaTitle && $wnd.changeMetaTitle(title);
	}-*/;

	private static native void nativeChangeUrl(String name) /*-{
		if (name && $wnd.history && $wnd.history.pushState) {
			try {
				$wnd.parent.history.pushState({}, "GeoGebra", name);
			} catch (e) {
				$wnd.history.pushState({}, "GeoGebra", name);
			}
		}
	}-*/;

	/**
	 * Opens GeoGebraTube material in a new window
	 * 
	 * @param url
	 *            GeoGebraTube url
	 */
	public native static void openWindow(String url)/*-{
		$wnd.open(url, '_blank');
	}-*/;

	/**
	 * Returns a string based on base 64 encoded value
	 * 
	 * @param base64
	 *            a base64 encoded string
	 * 
	 * @return decoded string
	 */
	public static native String decodeBase64(String base64)/*-{
		return $wnd.atob(base64);
	}-*/;

	/**
	 * 
	 * Returns a base64 encoding of the specified (binary) string
	 * 
	 * @param text
	 *            A binary string (obtained for instance by the FileReader API)
	 * @return a base64 encoded string.
	 */
	public static native String encodeBase64(String text)/*-{
		return $wnd.btoa(text);
	}-*/;

	public static void removeDefaultContextMenu(Element element) {
		setAllowContextMenu(element, false);
	}

	/**
	 * Allow or diallow context menu for an element.
	 * 
	 * @param element
	 *            element
	 * @param allow
	 *            whether to allow context menu
	 */
	public static native void setAllowContextMenu(Element element,
			boolean allow) /*-{
		if (element.addEventListener) {
			element.addEventListener("MSHoldVisual", function(e) {
				allow ? e.stopPropagation() : e.preventDefault();
			}, false);
			element.addEventListener('contextmenu', function(e) {
				allow ? e.stopPropagation() : e.preventDefault();
			}, false);
		}
	}-*/;

	public static native boolean isXWALK() /*-{
		return !!$wnd.ggbExamXWalkExtension;
	}-*/;

	public native static boolean isAndroid()/*-{
		var userAgent = $wnd.navigator.userAgent;
		if (userAgent) {
			return userAgent.indexOf("Android") != -1;
		}
		return false;
	}-*/;

	/**
	 * @return check this is an iPad browser
	 */
	public native static boolean isIPad()/*-{
		var userAgent = $wnd.navigator.userAgent;
		if (userAgent) {
			return userAgent.indexOf("iPad") != -1;
		}
		return false;
	}-*/;

	public static boolean isTabletBrowser() {
		return (isAndroid() || isIPad());
	}

	public static void setWebWorkerSupported(boolean b) {
		webWorkerSupported = b;
	}

	public static boolean webWorkerSupported() {
		return webWorkerSupported;
	}

	public static native int getScreenWidth() /*-{
		return $wnd.screen.width;
	}-*/;

	public static native int getScreenHeight() /*-{
		return $wnd.screen.height;
	}-*/;

	public static native boolean isEdge() /*-{
		return $wnd.navigator.userAgent.indexOf("Edge") > -1;
	}-*/;

	/**
	 * @param full
	 *            whether to go fullscreen
	 * @param element
	 *            element to be scaled
	 */
	public static native void toggleFullscreen(boolean full,
			JavaScriptObject element)/*-{
		var el = element || $doc.documentElement;
		if (full) { // current working methods
			if (el.requestFullscreen) {
				el.requestFullscreen();
			} else if ($doc.documentElement.msRequestFullscreen) {
				el.msRequestFullscreen();
			} else if ($doc.documentElement.mozRequestFullScreen) {
				el.mozRequestFullScreen();
			} else if ($doc.documentElement.webkitRequestFullScreen) {
				el.style.setProperty("width", "100%", "important");
				el.style.setProperty("height", "100%", "important");
				el.webkitRequestFullScreen();
				//Element.ALLOW_KEYBOARD_INPUT);
			}
		} else {
			if ($doc.exitFullscreen) {
				$doc.exitFullscreen();
			} else if ($doc.msExitFullscreen) {
				$doc.msExitFullscreen();
			} else if ($doc.mozCancelFullScreen) {
				$doc.mozCancelFullScreen();
			} else if ($doc.webkitCancelFullScreen) {
				$doc.webkitCancelFullScreen();
			}
		}
	}-*/;

	/**
	 * Register handler for fullscreen event.
	 * 
	 * @param callback
	 *            callback for fullscreen event
	 */
	public static native void addFullscreenListener(
			AsyncOperation<String> callback) /*-{
		var prefixes = [ "webkit", "ms", "moz" ];
		for ( var i in prefixes) {
			var prefix = prefixes[i];
			$doc
					.addEventListener(
							prefix + "fullscreenchange",
							(function(pfx) {
								return function(e) {
									callback.@org.geogebra.common.util.AsyncOperation::callback(*)(($doc[pfx+"FullscreenElement"] || $doc.mozFullScreen) ?  "true" : "false");
								}
							})(prefix));
		}

	}-*/;

	/**
	 * @return whether current window covers whole screen
	 */
	public static native boolean isCoveringWholeScreen()/*-{
		var height = $wnd.innerHeight;
		var width = $wnd.innerWidth;

		var screenHeight = screen.height - 5;
		var screenWidth = screen.width - 5;

		//$wnd.console.log("height: " + height, screenHeight);
		//$wnd.console.log("width: " + width, screenWidth);

		return height >= screenHeight && width >= screenWidth;
	}-*/;

	/**
	 * Add mutation observer to element and all its parents.
	 * 
	 * @param el
	 *            target element
	 * @param asyncOperation
	 *            callback
	 */
	public static native void addMutationObserver(Element el,
			AsyncOperation<String> asyncOperation) /*-{
		try {
			var current = el;
			while (current) {
				var observer = new MutationObserver(
						function(mutations) {
							mutations
									.forEach(function(mutation) {
										asyncOperation.@org.geogebra.common.util.AsyncOperation::callback(*)(mutation.type);
									});
						});
				observer.observe(current, {
					attributes : true,
					attributeFilter : [ "class", "style" ]
				});
				current = current.parentElement;
			}
		} catch (ex) {
			//Mutation observer not supported
		}
	}-*/;

	/**
	 * gets keycodes of iOS arrow keys iOS arrows have a different identifier
	 * than win and android
	 * 
	 * @param event
	 *            native key event
	 * @return JavaKeyCodes of arrow keys, -1 if pressed key was not an arrow
	 */
	public native static int getIOSArrowKeys(NativeEvent event) /*-{

		var key = event.key;
		@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("KeyDownEvent: " + key);
		switch (key) {
		case "UIKeyInputUpArrow":
			return @com.himamis.retex.editor.share.util.GWTKeycodes::KEY_UP;
		case "UIKeyInputDownArrow":
			return @com.himamis.retex.editor.share.util.GWTKeycodes::KEY_DOWN;
		case "UIKeyInputLeftArrow":
			return @com.himamis.retex.editor.share.util.GWTKeycodes::KEY_LEFT;
		case "UIKeyInputRightArrow":
			return @com.himamis.retex.editor.share.util.GWTKeycodes::KEY_RIGHT;
		default:
			return -1;
		}
	}-*/;

}
