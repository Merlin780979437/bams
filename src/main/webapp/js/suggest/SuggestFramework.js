
var sfw = new Array();
String.prototype.decode = function () {
	return decodeURI(this);
};
String.prototype.encode = function () {
	var _1 = "";
	if (this == "") {
		return this;
	}
	if (typeof encodeURIComponent == "function") {
		_1 = encodeURIComponent(this);
	} else {
		var _2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";
		var _3 = this.toUTF8();
		_1 = "";
		for (var i = 0; i < _3.length; i++) {
			if (_2.indexOf(_3.charAt(i)) == -1) {
				_1 += "%" + _3.charCodeAt(i).toHex();
			} else {
				_1 += _3.charAt(i);
			}
		}
	}
	return _1;
};
String.prototype.toHex = function () {
	var _5 = "0123456789ABCDEF";
	return _5.charAt(this.value >> 4) + _5.charAt(this.value & 15);
};
String.prototype.toUTF8 = function () {
	var a, b, i = 0;
	var _7 = "";
	while (i < this.length) {
		a = this.charCodeAt(i++);
		if (a >= 56320 && a < 57344) {
			continue;
		}
		if (a >= 55296 && a < 56320) {
			if (i >= this.length) {
				continue;
			}
			b = this.charCodeAt(i++);
			if (s < 56320 || a >= 56832) {
				continue;
			}
			a = ((a - 55296) << 10) + (b - 56320) + 65536;
		}
		if (a < 128) {
			_7 += String.fromCharCode(a);
		} else {
			if (a < 2048) {
				_7 += String.fromCharCode(192 + (a >> 6), 128 + (a & 63));
			} else {
				if (a < 65536) {
					_7 += String.fromCharCode(224 + (a >> 12), 128 + (a >> 6 & 63), 128 + (a & 63));
				} else {
					_7 += String.fromCharCode(240 + (a >> 18), 128 + (a >> 12 & 63), 128 + (a >> 6 & 63), 128 + (a & 63));
				}
			}
		}
	}
	return _7;
};
String.prototype.trim = function () {
	return this.replace(/^[\s]+|[\s]+$/, "");
};
function sfwCreate(_8) {
	if (sfw[_8].name && sfw[_8].action) {
		sfw[_8].inputContainer = document.getElementById(sfw[_8].name);
		sfw[_8].inputContainer.autocomplete = "off";
		sfw[_8].inputContainer.onblur = function () {
			sfwHideOutput(_8);
		};
		sfw[_8].inputContainer.onclick = function () {
			sfwShowOutput(_8);
		};
		sfw[_8].inputContainer.onfocus = function () {
			sfwShowOutput(_8);
		};
		sfw[_8].inputContainer.onkeypress = function (_9) {
			if (sfwGetKey(_9) == 13) {
				return false;
			}
		};
		sfw[_8].inputContainer.onkeydown = function (_a) {
			sfwProcessKeys(_8, _a);
		};
		sfw[_8].outputContainer = document.createElement("div");
		sfw[_8].outputContainer.id = sfw[_8].name + "_list";
		sfw[_8].outputContainer.className = "SuggestFramework_List";
		sfw[_8].outputContainer.style.position = "absolute";
		sfw[_8].outputContainer.style.zIndex = "1";
		if(sfw[_8].inputContainer.data == null || sfw[_8].inputContainer.data == "undefined"){
			sfw[_8].outputContainer.style.width = sfw[_8].inputContainer.clientWidth + "px";
		}else{
			sfw[_8].outputContainer.style.width = sfw[_8].inputContainer.data + "px";
		}
		sfw[_8].outputContainer.style.wordWrap = "break-word";
		sfw[_8].outputContainer.style.cursor = "default";
		sfw[_8].inputContainer.parentNode.insertBefore(sfw[_8].outputContainer, sfw[_8].inputContainer.nextSibling);
		sfw[_8].inputContainer.parentNode.insertBefore(document.createElement("br"), sfw[_8].outputContainer);
		if (sfw[_8].columns > 1 && sfw[_8].capture > 1) {
			sfw[_8].hiddenInput = document.createElement("input");
			sfw[_8].hiddenInput.id = "_" + sfw[_8].name;
			sfw[_8].hiddenInput.name = "_" + sfw[_8].name;
			sfw[_8].hiddenInput.type = "hidden";
			sfw[_8].inputContainer.parentNode.insertBefore(sfw[_8].hiddenInput, sfw[_8].inputContainer.nextSibling);
		}
		if (!sfwCreateConnection()) {
			sfw[_8].proxy = document.createElement("iframe");
			sfw[_8].proxy.id = "proxy";
			sfw[_8].proxy.style.width = "0";
			sfw[_8].proxy.style.height = "0";
			sfw[_8].proxy.style.display = "none";
			document.body.appendChild(sfw[_8].proxy);
			if (window.frames && window.frames["proxy"]) {
				sfw[_8].proxy = window.frames["proxy"];
			} else {
				if (document.getElementById("proxy").contentWindow) {
					sfw[_8].proxy = document.getElementById("proxy").contentWindow;
				} else {
					sfw[_8].proxy = document.getElementById("proxy");
				}
			}
		}
		sfwHideOutput(_8);
		sfwThrottle(_8);
	} else {
		throw "Suggest Framework Error: Instance \"" + sfw[_8].name + "\" not initialized";
	}
}
function sfwCreateConnection() {
	var _b;
	try {
		_b = new ActiveXObject("Microsoft.XMLHTTP");
	}
	catch (e) {
		if (typeof XMLHttpRequest != "undefined") {
			_b = new XMLHttpRequest();
		}
	}
	return _b;
}
function sfwGetKey(e) {
	return ((window.event) ? window.event.keyCode : e.which);
}
function sfwHideOutput(_d) {
	sfw[_d].outputContainer.style.display = "none";
}
function sfwHighlight(_e, _f) {
	sfw[_e].suggestionsIndex = _f;
	for (var i in sfw[_e].suggestions) {
		var _11 = document.getElementById(sfw[_e].name + "_suggestions[" + i + "]").getElementsByTagName("td");
		for (var j in _11) {
			_11[j].className = "SuggestFramework_Normal";
		}
	}
	var _13 = document.getElementById(sfw[_e].name + "_suggestions[" + sfw[_e].suggestionsIndex + "]").getElementsByTagName("td");
	for (var i in _13) {
		_13[i].className = "SuggestFramework_Highlighted";
	}
}
function sfwIsHidden(_15) {
	return ((sfw[_15].outputContainer.style.display == "none") ? true : false);
}
function sfwProcessKeys(_16, e) {
	var _18 = 40;
	var _19 = 38;
	var _1a = 9;
	var _1b = 13;
	var _1c = 27;
	if (!sfwIsHidden(_16)) {
		switch (sfwGetKey(e)) {
		  case _18:
			sfwSelectNext(_16);
			return;
		  case _19:
			sfwSelectPrevious(_16);
			return;
		  case _1b:
			sfwSelectThis(_16);
			return;
		  case _1a:
			sfwSelectThis(_16);
			return;
		  case _1c:
			sfwHideOutput(_16);
			return;
		  default:
			return;
		}
	}
}
function sfwProcessProxyRequest(_1d) {
	var _1e = ((sfw[_1d].proxy.document) ? sfw[_1d].proxy.document : sfw[_1d].proxy.contentDocument);
	_1e = _1e.body.innerHTML.replace(/\r|\n/g, " ").trim();
	if (typeof eval(_1e) == "object") {
		sfwSuggest(_1d, eval(_1e));
	} else {
		setTimeout("sfwProcessProxyRequest(" + _1d + ")", 100);
	}
}
function sfwProcessRequest(_1f) {
	if (sfw[_1f].connection.readyState == 4) {
		if (sfw[_1f].connection.status == 200) {
			sfwSuggest(_1f, eval(sfw[_1f].connection.responseText));
		}
	}
}
function sfwQuery(_20) {
	sfwThrottle(_20);
	var _21 = sfw[_20].inputContainer.value;
	if (_21 == "" || _21 == sfw[_20].previous) {
		return;
	}
	sfw[_20].previous = _21;
	var url = sfw[_20].action + "?type=" + sfw[_20].name + "&q=" + _21.trim().encode();
	url = encodeURI(url);
	sfwRequest(_20, url);
}
function sfwRequest(_23, url) {
	if (sfw[_23].connection = sfwCreateConnection()) {
		sfw[_23].connection.onreadystatechange = function () {
			sfwProcessRequest(_23);
		};
		sfw[_23].connection.open("GET", url, true);
		sfw[_23].connection.send(null);
	} else {
		sfw[_23].proxy.location.replace(url);
		sfwProcessProxyRequest(_23);
	}
}
function sfwSelectThis(_25, _26) {
	if (sfw[_25].columns > 1 && sfw[_25].capture > 1) {
		sfw[_25].hiddenInput.value = sfw[_25].suggestions[sfw[_25].suggestionsIndex][sfw[_25].capture - 1];
	}
	if (!isNaN(_26)) {
		sfw[_25].suggestionsIndex = _26;
	}
	var _27 = sfw[_25].suggestions[sfw[_25].suggestionsIndex];
	if (sfw[_25].columns > 1) {
		_27 = _27[0];
	}
	sfw[_25].inputContainer.value = _27;
	sfw[_25].previous = _27;
	sfwHideOutput(_25);
}
function sfwSelectNext(_28) {
	sfwSetTextSelectionRange(_28);
	if (typeof sfw[_28].suggestions[(sfw[_28].suggestionsIndex + 1)] != "undefined") {
		if (typeof sfw[_28].suggestions[sfw[_28].suggestionsIndex] != "undefined") {
			document.getElementById(sfw[_28].name + "_suggestions[" + sfw[_28].suggestionsIndex + "]").className = "SuggestFramework_Normal";
		}
		sfw[_28].suggestionsIndex++;
		sfwHighlight(_28, sfw[_28].suggestionsIndex);
	}
}
function sfwSelectPrevious(_29) {
	sfwSetTextSelectionRange(_29);
	if (typeof sfw[_29].suggestions[(sfw[_29].suggestionsIndex - 1)] != "undefined") {
		if (typeof sfw[_29].suggestions[sfw[_29].suggestionsIndex] != "undefined") {
			document.getElementById(sfw[_29].name + "_suggestions[" + sfw[_29].suggestionsIndex + "]").className = "SuggestFramework_Normal";
		}
		sfw[_29].suggestionsIndex--;
		sfwHighlight(_29, sfw[_29].suggestionsIndex);
	}
}
function sfwSetTextSelectionRange(_2a, _2b, end) {
	if (!_2b) {
		var _2d = sfw[_2a].inputContainer.value.length;
	}
	if (!end) {
		var end = sfw[_2a].inputContainer.value.length;
	}
	if (sfw[_2a].inputContainer.setSelectionRange) {
		sfw[_2a].inputContainer.setSelectionRange(_2d, end);
	} else {
		if (sfw[_2a].inputContainer.createTextRange) {
			var _2f = sfw[_2a].inputContainer.createTextRange();
			_2f.moveStart("character", _2d);
			_2f.moveEnd("character", end);
			_2f.select();
		}
	}
}
function sfwShowOutput(_30) {
	if (typeof sfw[_30].suggestions != "undefined" && sfw[_30].suggestions.length) {
		sfw[_30].outputContainer.style.display = "block";
	}
}
function sfwSuggest(_31, _32) {
	sfw[_31].suggestions = _32;
	sfw[_31].suggestionsIndex = -1;
	sfw[_31].outputContainer.innerHTML = "";
	var _33 = "<table style=\"width: 100%; margin: 0; padding: 0\" cellspacing=\"0\" cellpadding=\"0\">";
	if (sfw[_31].heading && sfw[_31].suggestions.length) {
		var _34 = sfw[_31].suggestions.shift();
		var _35 = "<thead>";
		var _36 = "<tr>";
		for (var i = 0; i < sfw[_31].columns; i++) {
			var _38 = (String)((sfw[_31].columns > 1) ? _34[i] : _34);
			var _39 = "<td class=\"SuggestFramework_Heading\"";
			if (sfw[_31].columns > 1 && i == sfw[_31].columns - 1) {
				_39 += " style=\"text-align: right\"";
			}
			_39 += ">" + _38.decode().trim() + "</td>";
			_36 += _39;
		}
		_36 += "</tr>";
		_35 += _36;
		_35 += "</thead>";
		_33 += _35;
	}
	var _3a = "<tbody>";
	for (var i in sfw[_31].suggestions) {
		var _3c = "<tr id=\"" + sfw[_31].name + "_suggestions[" + i + "]\">";
		for (var j = 0; j < sfw[_31].columns; j++) {
			var _3e = (String)((sfw[_31].columns > 1) ? sfw[_31].suggestions[i][j] : sfw[_31].suggestions[i]);
			var _3f = "<td class=\"SuggestFramework_Normal\"";
			if (sfw[_31].columns > 1 && j == sfw[_31].columns - 1) {
				_3f += " style=\"text-align: right\"";
			}
			_3f += ">" + _3e.decode().trim() + "</td>";
			_3c += _3f;
		}
		_3c += "</tr>";
		_33 += _3c;
	}
	_3a += "</tbody>";
	_33 += _3a;
	_33 += "</table>";
	sfw[_31].outputContainer.innerHTML = _33;
	for (var i in sfw[_31].suggestions) {
		var row = document.getElementById(sfw[_31].name + "_suggestions[" + i + "]");
		row.onmouseover = new Function("sfwHighlight(" + _31 + ", " + i + ")");
		row.onmousedown = new Function("sfwSelectThis(" + _31 + ", " + i + ")");
	}
	sfwShowOutput(_31);
}
function sfwThrottle(_42) {
	setTimeout("sfwQuery(" + _42 + ")", sfw[_42].delay);
}
function initializeSuggestFramework() {
	function getAttributeByName(_43, _44) {
		if (typeof NamedNodeMap != "undefined") {
			if (_43.attributes.getNamedItem(_44)) {
				return _43.attributes.getNamedItem(_44).value;
			}
		} else {
			return _43.getAttribute(_44);
		}
	}
	var _45 = document.getElementsByTagName("input");
	try {
		for (var _46 = 0; _46 < _45.length; _46++) {
			if (getAttributeByName(_45[_46], "name") && getAttributeByName(_45[_46], "type") == "text" && getAttributeByName(_45[_46], "action")) {
				sfw[_46] = new Object();
				sfw[_46].action = getAttributeByName(_45[_46], "action");
				sfw[_46].capture = 1;
				sfw[_46].columns = 1;
				sfw[_46].delay = 1000;
				sfw[_46].heading = false;
				sfw[_46].name = getAttributeByName(_45[_46], "name");
				if (getAttributeByName(_45[_46], "capture")) {
					sfw[_46].capture = getAttributeByName(_45[_46], "capture");
				}
				if (getAttributeByName(_45[_46], "columns")) {
					sfw[_46].columns = getAttributeByName(_45[_46], "columns");
				}
				if (getAttributeByName(_45[_46], "delay")) {
					sfw[_46].delay = getAttributeByName(_45[_46], "delay");
				}
				if (getAttributeByName(_45[_46], "heading")) {
					sfw[_46].heading = getAttributeByName(_45[_46], "heading");
				}
				sfwCreate(_46);
			}
		}
	}
	catch (e) {
	}
}

