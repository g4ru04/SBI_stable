/*!
 * jquery.fancytree.js
 * Dynamic tree view control, with support for lazy loading of branches.
 * https://github.com/mar10/fancytree/
 *
 * Copyright (c) 2006-2014, Martin Wendt (http://wwWendt.de)
 * Released under the MIT license
 * https://github.com/mar10/fancytree/wiki/LicenseInfo
 *
 * @version @VERSION
 * @date @DATE
 */

/** Core Fancytree module.
 */



;(function($, window, document, undefined) {
"use strict";


if ( $.ui.fancytree && $.ui.fancytree.version ) {
	$.ui.fancytree.warn("Fancytree: ignored duplicate include");
	return;
}




function _raiseNotImplemented(msg){
	msg = msg || "";
	$.error("Not implemented: " + msg);
}

function _assert(cond, msg){
	
	if(!cond){
		msg = msg ? ": " + msg : "";
		$.error("Assertion failed" + msg);
	}
}

function consoleApply(method, args){
	var i, s,
		fn = window.console ? window.console[method] : null;

	if(fn){
		if(fn.apply){
			//fn.apply(window.console, args);
		}else{
			
			s = "";
			for( i=0; i<args.length; i++){
				s += args[i];
			}
			fn(s);
		}
	}
}


function _isNode(x){
	return !!(x.tree && x.statusNodeType !== undefined);
}

/** Return true if dotted version string is equal or higher than requested version.
 *
 * See http://jsfiddle.net/mar10/FjSAN/
 */
function isVersionAtLeast(dottedVersion, major, minor, patch){
	var i, v, t,
		verParts = $.map($.trim(dottedVersion).split("."), function(e){ return parseInt(e, 10); }),
		testParts = $.map(Array.prototype.slice.call(arguments, 1), function(e){ return parseInt(e, 10); });

	for( i = 0; i < testParts.length; i++ ){
		v = verParts[i] || 0;
		t = testParts[i] || 0;
		if( v !== t ){
			return ( v > t );
		}
	}
	return true;
}

/** Return a wrapper that calls sub.methodName() and exposes
 *  this        : tree
 *  this._local : tree.ext.EXTNAME
 *  this._super : base.methodName()
 */
function _makeVirtualFunction(methodName, tree, base, extension, extName){
	
	
	
	
	
	
	var proxy = (function(){
		var prevFunc = tree[methodName],      
			baseFunc = extension[methodName], //
			_local = tree.ext[extName],
			_super = function(){
				return prevFunc.apply(tree, arguments);
			};

		
		return function(){
			var prevLocal = tree._local,
				prevSuper = tree._super;
			try{
				tree._local = _local;
				tree._super = _super;
				return  baseFunc.apply(tree, arguments);
			}finally{
				tree._local = prevLocal;
				tree._super = prevSuper;
			}
		};
	})(); 
	return proxy;
}

/**
 * Subclass `base` by creating proxy functions
 */
function _subclassObject(tree, base, extension, extName){
	
	for(var attrName in extension){
		if(typeof extension[attrName] === "function"){
			if(typeof tree[attrName] === "function"){
				
				tree[attrName] = _makeVirtualFunction(attrName, tree, base, extension, extName);
			}else if(attrName.charAt(0) === "_"){
				
				tree.ext[extName][attrName] = _makeVirtualFunction(attrName, tree, base, extension, extName);
			}else{
				$.error("Could not override tree." + attrName + ". Use prefix '_' to create tree." + extName + "._" + attrName);
			}
		}else{
			
			if(attrName !== "options"){
				tree.ext[extName][attrName] = extension[attrName];
			}
		}
	}
}


function _getResolvedPromise(context, argArray){
	if(context === undefined){
		return $.Deferred(function(){this.resolve();}).promise();
	}else{
		return $.Deferred(function(){this.resolveWith(context, argArray);}).promise();
	}
}


function _getRejectedPromise(context, argArray){
	if(context === undefined){
		return $.Deferred(function(){this.reject();}).promise();
	}else{
		return $.Deferred(function(){this.rejectWith(context, argArray);}).promise();
	}
}


function _makeResolveFunc(deferred, context){
	return function(){
		deferred.resolveWith(context);
	};
}


function _getElementDataAsDict($el){
	
	var d = $.extend({}, $el.data()),
		json = d.json;
	delete d.fancytree; 
	if( json ) {
		delete d.json;
		
		d = $.extend(d, json);
	}
	return d;
}



function _makeNodeTitleMatcher(s){
	s = s.toLowerCase();
	return function(node){
		return node.title.toLowerCase().indexOf(s) >= 0;
	};
}

var i,
	FT = null, 
	ENTITY_MAP = {"&": "&amp;", "<": "&lt;", ">": "&gt;", "\"": "&quot;", "'": "&#39;", "/": "&#x2F;"},

	CLASS_ATTRS = "active expanded focus folder hideCheckbox lazy selected unselectable".split(" "),
	CLASS_ATTR_MAP = {},

	NODE_ATTRS = "expanded extraClasses folder hideCheckbox key lazy refKey selected title tooltip unselectable".split(" "),
	NODE_ATTR_MAP = {},
	
	NONE_NODE_DATA_MAP = {"active": true, "children": true, "data": true, "focus": true};

for(i=0; i<CLASS_ATTRS.length; i++){ CLASS_ATTR_MAP[CLASS_ATTRS[i]] = true; }
for(i=0; i<NODE_ATTRS.length; i++){ NODE_ATTR_MAP[NODE_ATTRS[i]] = true; }





/**
 * Creates a new FancytreeNode
 *
 * @class FancytreeNode
 * @classdesc A FancytreeNode represents the hierarchical data model and operations.
 *
 * @param {FancytreeNode} parent
 * @param {NodeData} obj
 *
 * @property {Fancytree} tree The tree instance
 * @property {FancytreeNode} parent The parent node
 * @property {string} key Node id (must be unique inside the tree)
 * @property {string} title Display name (may contain HTML)
 * @property {object} data Contains all extra data that was passed on node creation
 * @property {FancytreeNode[] | null | undefined} children Array of child nodes.<br>
 *     For lazy nodes, null or undefined means 'not yet loaded'. Use an empty array
 *     to define a node that has no children.
 * @property {boolean} expanded Use isExpanded(), setExpanded() to access this property.
 * @property {string} extraClasses Addtional CSS classes, added to the node's `&lt;span>`
 * @property {boolean} folder Folder nodes have different default icons and click behavior.<br>
 *     Note: Also non-folders may have children.
 * @property {string} statusNodeType null or type of temporarily generated system node like 'loading', or 'error'.
 * @property {boolean} lazy True if this node is loaded on demand, i.e. on first expansion.
 * @property {boolean} selected Use isSelected(), setSelected() to access this property.
 * @property {string} tooltip Alternative description used as hover banner
 */
function FancytreeNode(parent, obj){
	var i, l, name, cl;

	this.parent = parent;
	this.tree = parent.tree;
	this.ul = null;
	this.li = null;  
	this.statusNodeType = null; 
	this._isLoading = false;    
	this._error = null;         
	this.data = {};

	
	
	for(i=0, l=NODE_ATTRS.length; i<l; i++){
		name = NODE_ATTRS[i];
		this[name] = obj[name];
	}
	
	if(obj.data){
		$.extend(this.data, obj.data);
	}
	
	for(name in obj){
		if(!NODE_ATTR_MAP[name] && !$.isFunction(obj[name]) && !NONE_NODE_DATA_MAP[name]){
			
			this.data[name] = obj[name];
		}
	}

	
	if( this.key == null ){ 
		if( this.tree.options.defaultKey ) {
			this.key = this.tree.options.defaultKey(this);
			_assert(this.key, "defaultKey() must return a unique key");
		} else {
			this.key = "_" + (FT._nextNodeKey++);
		}
	} else {
		this.key = "" + this.key; 
	}

	
	
	
	if(obj.active){
		_assert(this.tree.activeNode === null, "only one active node allowed");
		this.tree.activeNode = this;
	}
	if( obj.selected ){ 
		this.tree.lastSelectedNode = this;
	}
	
	
	this.children = null;
	cl = obj.children;
	if(cl && cl.length){
		this._setChildren(cl);
	}
	

	this.tree._callHook("treeRegisterNode", this.tree, true, this);

}


FancytreeNode.prototype = {
	
	_findDirectChild: function(ptr){
		var i, l,
			cl = this.children;

		if(cl){
			if(typeof ptr === "string"){
				for(i=0, l=cl.length; i<l; i++){
					if(cl[i].key === ptr){
						return cl[i];
					}
				}
			}else if(typeof ptr === "number"){
				return this.children[ptr];
			}else if(ptr.parent === this){
				return ptr;
			}
		}
		return null;
	},
	
	
	
	_setChildren: function(children){
		_assert(children && (!this.children || this.children.length === 0), "only init supported");
		this.children = [];
		for(var i=0, l=children.length; i<l; i++){
			this.children.push(new FancytreeNode(this, children[i]));
		}
	},
	/**
	 * Append (or insert) a list of child nodes.
	 *
	 * @param {NodeData[]} children array of child node definitions (also single child accepted)
	 * @param {FancytreeNode | string | Integer} [insertBefore] child node (or key or index of such).
	 *     If omitted, the new children are appended.
	 * @returns {FancytreeNode} first child added
	 *
	 * @see FancytreeNode#applyPatch
	 */
	addChildren: function(children, insertBefore){
		var i, l, pos,
			firstNode = null,
			nodeList = [];

		if($.isPlainObject(children) ){
			children = [children];
		}
		if(!this.children){
			this.children = [];
		}
		for(i=0, l=children.length; i<l; i++){
			nodeList.push(new FancytreeNode(this, children[i]));
		}
		firstNode = nodeList[0];
		if(insertBefore == null){
			this.children = this.children.concat(nodeList);
		}else{
			insertBefore = this._findDirectChild(insertBefore);
			pos = $.inArray(insertBefore, this.children);
			_assert(pos >= 0, "insertBefore must be an existing child");
			
			this.children.splice.apply(this.children, [pos, 0].concat(nodeList));
		}
		if( !this.parent || this.parent.ul || this.tr ){
			
			this.render();
		}
		if( this.tree.options.selectMode === 3 ){
			this.fixSelection3FromEndNodes();
		}
		return firstNode;
	},
	/**
	 * Append or prepend a node, or append a child node.
	 *
	 * This a convenience function that calls addChildren()
	 *
	 * @param {NodeData} node node definition
	 * @param {string} [mode=child] 'before', 'after', or 'child' ('over' is a synonym for 'child')
	 * @returns {FancytreeNode} new node
	 */
	addNode: function(node, mode){
		if(mode === undefined || mode === "over"){
			mode = "child";
		}
		switch(mode){
		case "after":
			return this.getParent().addChildren(node, this.getNextSibling());
		case "before":
			return this.getParent().addChildren(node, this);
		case "child":
		case "over":
			return this.addChildren(node);
		}
		_assert(false, "Invalid mode: " + mode);
	},
	/**
	 * Append new node after this.
	 *
	 * This a convenience function that calls addNode(node, 'after')
	 *
	 * @param {NodeData} node node definition
	 * @returns {FancytreeNode} new node
	 */
	appendSibling: function(node){
		return this.addNode(node, "after");
	},
	/**
	 * Modify existing child nodes.
	 *
	 * @param {NodePatch} patch
	 * @returns {$.Promise}
	 * @see FancytreeNode#addChildren
	 */
	applyPatch: function(patch) {
		
		if(patch === null){
			this.remove();
			return _getResolvedPromise(this);
		}
		
		
		var name, promise, v,
			IGNORE_MAP = { children: true, expanded: true, parent: true }; 

		for(name in patch){
			v = patch[name];
			if( !IGNORE_MAP[name] && !$.isFunction(v)){
				if(NODE_ATTR_MAP[name]){
					this[name] = v;
				}else{
					this.data[name] = v;
				}
			}
		}
		
		if(patch.hasOwnProperty("children")){
			this.removeChildren();
			if(patch.children){ 
				
				this._setChildren(patch.children);
			}
			
		}
		if(this.isVisible()){
			this.renderTitle();
			this.renderStatus();
		}
		
		if(patch.hasOwnProperty("expanded")){
			promise = this.setExpanded(patch.expanded);
		}else{
			promise = _getResolvedPromise(this);
		}
		return promise;
	},
	/** Collapse all sibling nodes.
	 * @returns {$.Promise}
	 */
	collapseSiblings: function() {
		return this.tree._callHook("nodeCollapseSiblings", this);
	},
	/** Copy this node as sibling or child of `node`.
	 *
	 * @param {FancytreeNode} node source node
	 * @param {string} mode 'before' | 'after' | 'child'
	 * @param {Function} [map] callback function(NodeData) that could modify the new node
	 * @returns {FancytreeNode} new
	 */
	copyTo: function(node, mode, map) {
		return node.addNode(this.toDict(true, map), mode);
	},
	/** Count direct and indirect children.
	 *
	 * @param {boolean} [deep=true] pass 'false' to only count direct children
	 * @returns {int} number of child nodes
	 */
	countChildren: function(deep) {
		var cl = this.children, i, l, n;
		if( !cl ){
			return 0;
		}
		n = cl.length;
		if(deep !== false){
			for(i=0, l=n; i<l; i++){
				n += cl[i].countChildren();
			}
		}
		return n;
	},
	
	/** Write to browser console if debugLevel >= 2 (prepending node info)
	 *
	 * @param {*} msg string or object or array of such
	 */
	debug: function(msg){
		if( this.tree.options.debugLevel >= 2 ) {
			Array.prototype.unshift.call(arguments, this.toString());
			consoleApply("debug", arguments);
		}
	},
	/** Deprecated.
	 * @deprecated since 2014-02-16. Use resetLazy() instead.
	 */
	discard: function(){
		this.warn("FancytreeNode.discard() is deprecated since 2014-02-16. Use .resetLazy() instead.");
		return this.resetLazy();
	},
	
	/**Find all nodes that contain `match` in the title.
	 *
	 * @param {string | function(node)} match string to search for, of a function that
	 * returns `true` if a node is matched.
	 * @returns {FancytreeNode[]} array of nodes (may be empty)
	 * @see FancytreeNode#findAll
	 */
	findAll: function(match) {
		match = $.isFunction(match) ? match : _makeNodeTitleMatcher(match);
		var res = [];
		this.visit(function(n){
			if(match(n)){
				res.push(n);
			}
		});
		return res;
	},
	/**Find first node that contains `match` in the title (not including self).
	 *
	 * @param {string | function(node)} match string to search for, of a function that
	 * returns `true` if a node is matched.
	 * @returns {FancytreeNode} matching node or null
	 * @example
	 * <b>fat</b> text
	 */
	findFirst: function(match) {
		match = $.isFunction(match) ? match : _makeNodeTitleMatcher(match);
		var res = null;
		this.visit(function(n){
			if(match(n)){
				res = n;
				return false;
			}
		});
		return res;
	},
	
	_changeSelectStatusAttrs: function (state) {
		var changed = false;

		switch(state){
		case false:
			changed = ( this.selected || this.partsel );
			this.selected = false;
			this.partsel = false;
			break;
		case true:
			changed = ( !this.selected || !this.partsel );
			this.selected = true;
			this.partsel = true;
			break;
		case undefined:
			changed = ( this.selected || !this.partsel );
			this.selected = false;
			this.partsel = true;
			break;
		default:
			_assert(false, "invalid state: " + state);
		}
		
		if( changed ){
			this.renderStatus();
		}
		return changed;
	},
	/**
	 * Fix selection status, after this node was (de)selected in multi-hier mode.
	 * This includes (de)selecting all children.
	 */
	fixSelection3AfterClick: function() {
		var flag = this.isSelected();



		this.visit(function(node){
			node._changeSelectStatusAttrs(flag);
		});
		this.fixSelection3FromEndNodes();
	},
	/**
	 * Fix selection status for multi-hier mode.
	 * Only end-nodes are considered to update the descendants branch and parents.
	 * Should be called after this node has loaded new children or after
	 * children have been modified using the API.
	 */
	fixSelection3FromEndNodes: function() {

		_assert(this.tree.options.selectMode === 3, "expected selectMode 3");

		
		
		function _walk(node){
			var i, l, child, s, state, allSelected,someSelected,
				children = node.children;

			if( children && children.length ){
				
				allSelected = true;
				someSelected = false;

				for( i=0, l=children.length; i<l; i++ ){
					child = children[i];
					
					s = _walk(child);
					if( s !== false ) {
						someSelected = true;
					}
					if( s !== true ) {
						allSelected = false;
					}
				}
				state = allSelected ? true : (someSelected ? undefined : false);
			}else{
				

				state = !!node.selected;
			}
			node._changeSelectStatusAttrs(state);
			return state;
		}
		_walk(this);

		
		this.visitParents(function(node){
			var i, l, child, state,
				children = node.children,
				allSelected = true,
				someSelected = false;

			for( i=0, l=children.length; i<l; i++ ){
				child = children[i];
				
				
				if( child.selected || child.partsel ) {
					someSelected = true;
				}
				if( !child.unselectable && !child.selected ) {
					allSelected = false;
				}
			}
			state = allSelected ? true : (someSelected ? undefined : false);
			node._changeSelectStatusAttrs(state);
		});
	},
	
	/**
	 * Update node data. If dict contains 'children', then also replace
	 * the hole sub tree.
	 * @param {NodeData} dict
	 *
	 * @see FancytreeNode#addChildren
	 * @see FancytreeNode#applyPatch
	 */
	fromDict: function(dict) {
		
		for(var name in dict){
			if(NODE_ATTR_MAP[name]){
				
				this[name] = dict[name];
			}else if(name === "data"){
				
				$.extend(this.data, dict.data);
			}else if(!$.isFunction(dict[name]) && !NONE_NODE_DATA_MAP[name]){
				
				this.data[name] = dict[name];
			}
		}
		if(dict.children){
			
			this.removeChildren();
			this.addChildren(dict.children);
		}
		this.renderTitle();
/*
		var children = dict.children;
		if(children === undefined){
			this.data = $.extend(this.data, dict);
			this.render();
			return;
		}
		dict = $.extend({}, dict);
		dict.children = undefined;
		this.data = $.extend(this.data, dict);
		this.removeChildren();
		this.addChild(children);
*/
	},
	/** Return the list of child nodes (undefined for unexpanded lazy nodes).
	 * @returns {FancytreeNode[] | undefined}
	 */
	getChildren: function() {
		if(this.hasChildren() === undefined){ 
			return undefined; 
		}
		return this.children;
	},
	/** Return the first child node or null.
	 * @returns {FancytreeNode | null}
	 */
	getFirstChild: function() {
		return this.children ? this.children[0] : null;
	},
	/** Return the 0-based child index.
	 * @returns {int}
	 */
	getIndex: function() {

		return $.inArray(this, this.parent.children); 
	},
	/** Return the hierarchical child index (1-based, e.g. '3.2.4').
	 * @returns {string}
	 */
	getIndexHier: function(separator) {
		separator = separator || ".";
		var res = [];
		$.each(this.getParentList(false, true), function(i, o){
			res.push(o.getIndex() + 1);
		});
		return res.join(separator);
	},
	/** Return the parent keys separated by options.keyPathSeparator, e.g. "id_1/id_17/id_32".
	 * @param {boolean} [excludeSelf=false]
	 * @returns {string}
	 */
	getKeyPath: function(excludeSelf) {
		var path = [],
			sep = this.tree.options.keyPathSeparator;
		this.visitParents(function(n){
			if(n.parent){
				path.unshift(n.key);
			}
		}, !excludeSelf);
		return sep + path.join(sep);
	},
	/** Return the last child of this node or null.
	 * @returns {FancytreeNode | null}
	 */
	getLastChild: function() {
		return this.children ? this.children[this.children.length - 1] : null;
	},
	/** Return node depth. 0: System root node, 1: visible top-level node, 2: first sub-level, ... .
	 * @returns {int}
	 */
	getLevel: function() {
		var level = 0,
			dtn = this.parent;
		while( dtn ) {
			level++;
			dtn = dtn.parent;
		}
		return level;
	},
	/** Return the successor node (under the same parent) or null.
	 * @returns {FancytreeNode | null}
	 */
	getNextSibling: function() {
		
		if( this.parent ){
			var i, l,
				ac = this.parent.children;

			for(i=0, l=ac.length-1; i<l; i++){ 
				if( ac[i] === this ){
					return ac[i+1];
				}
			}
		}
		return null;
	},
	/** Return the parent node (null for the system root node).
	 * @returns {FancytreeNode | null}
	 */
	getParent: function() {
		
		return this.parent;
	},
	/** Return an array of all parent nodes (top-down).
	 * @param {boolean} [includeRoot=false] Include the invisible system root node.
	 * @param {boolean} [includeSelf=false] Include the node itself.
	 * @returns {FancytreeNode[]}
	 */
	getParentList: function(includeRoot, includeSelf) {
		var l = [],
			dtn = includeSelf ? this : this.parent;
		while( dtn ) {
			if( includeRoot || dtn.parent ){
				l.unshift(dtn);
			}
			dtn = dtn.parent;
		}
		return l;
	},
	/** Return the predecessor node (under the same parent) or null.
	 * @returns {FancytreeNode | null}
	 */
	getPrevSibling: function() {
		if( this.parent ){
			var i, l,
				ac = this.parent.children;

			for(i=1, l=ac.length; i<l; i++){ 
				if( ac[i] === this ){
					return ac[i-1];
				}
			}
		}
		return null;
	},
	/** Return true if node has children. Return undefined if not sure, i.e. the node is lazy and not yet loaded).
	 * @returns {boolean | undefined}
	 */
	hasChildren: function() {
		if(this.lazy){
			if(this.children == null ){
				
				return undefined;
			}else if(this.children.length === 0){
				
				return false;
			}else if(this.children.length === 1 && this.children[0].isStatusNode() ){
				
				return undefined;
			}
			return true;
		}
		return !!this.children;
	},
	/** Return true if node has keyboard focus.
	 * @returns {boolean}
	 */
	hasFocus: function() {
		return (this.tree.hasFocus() && this.tree.focusNode === this);
	},
	/** Write to browser console if debugLevel >= 1 (prepending node info)
	 *
	 * @param {*} msg string or object or array of such
	 */
	info: function(msg){
		if( this.tree.options.debugLevel >= 1 ) {
			Array.prototype.unshift.call(arguments, this.toString());
			consoleApply("info", arguments);
		}
	},
	/** Return true if node is active (see also FancytreeNode#isSelected).
	 * @returns {boolean}
	 */
	isActive: function() {
		return (this.tree.activeNode === this);
	},
	/** Return true if node is a direct child of otherNode.
	 * @param {FancytreeNode} otherNode
	 * @returns {boolean}
	 */
	isChildOf: function(otherNode) {
		return (this.parent && this.parent === otherNode);
	},
	/** Return true, if node is a direct or indirect sub node of otherNode.
	 * @param {FancytreeNode} otherNode
	 * @returns {boolean}
	 */
	isDescendantOf: function(otherNode) {
		if(!otherNode || otherNode.tree !== this.tree){
			return false;
		}
		var p = this.parent;
		while( p ) {
			if( p === otherNode ){
				return true;
			}
			p = p.parent;
		}
		return false;
	},
	/** Return true if node is expanded.
	 * @returns {boolean}
	 */
	isExpanded: function() {
		return !!this.expanded;
	},
	/** Return true if node is the first node of its parent's children.
	 * @returns {boolean}
	 */
	isFirstSibling: function() {
		var p = this.parent;
		return !p || p.children[0] === this;
	},
	/** Return true if node is a folder, i.e. has the node.folder attribute set.
	 * @returns {boolean}
	 */
	isFolder: function() {
		return !!this.folder;
	},
	/** Return true if node is the last node of its parent's children.
	 * @returns {boolean}
	 */
	isLastSibling: function() {
		var p = this.parent;
		return !p || p.children[p.children.length-1] === this;
	},
	/** Return true if node is lazy (even if data was already loaded)
	 * @returns {boolean}
	 */
	isLazy: function() {
		return !!this.lazy;
	},
	/** Return true if node is lazy and loaded. For non-lazy nodes always return true.
	 * @returns {boolean}
	 */
	isLoaded: function() {
		return !this.lazy || this.hasChildren() !== undefined; 
	},
	/** Return true if children are currently beeing loaded, i.e. a Ajax request is pending.
	 * @returns {boolean}
	 */
	isLoading: function() {
		return !!this._isLoading;
	},
	/** Return true if this is the (invisible) system root node.
	 * @returns {boolean}
	 */
	isRoot: function() {
		return (this.tree.rootNode === this);
	},
	/** Return true if node is selected, i.e. has a checkmark set (see also FancytreeNode#isActive).
	 * @returns {boolean}
	 */
	isSelected: function() {
		return !!this.selected;
	},
	/** Return true if this node is a temporarily generated system node like
	 * 'loading', or 'error' (node.statusNodeType contains the type).
	 * @returns {boolean}
	 */
	isStatusNode: function() {
		return !!this.statusNodeType;
	},
	/** Return true if node is lazy and not yet loaded. For non-lazy nodes always return false.
	 * @returns {boolean}
	 */
	isUndefined: function() {
		return this.hasChildren() === undefined; 
	},
	/** Return true if all parent nodes are expanded. Note: this does not check
	 * whether the node is scrolled into the visible part of the screen.
	 * @returns {boolean}
	 */
	isVisible: function() {
		var i, l,
			parents = this.getParentList(false, false);

		for(i=0, l=parents.length; i<l; i++){
			if( ! parents[i].expanded ){ return false; }
		}
		return true;
	},
	/** Deprecated.
	 * @deprecated since 2014-02-16: use load() instead.
	 */
	lazyLoad: function(discard) {
		this.warn("FancytreeNode.lazyLoad() is deprecated since 2014-02-16. Use .load() instead.");
		return this.load(discard);
	},
	/**
	 * Load all children of a lazy node.
	 * @param {boolean} [forceReload=false] Pass true to discard any existing nodes before.
	 * @returns {$.Promise}
	 */
	load: function(forceReload) {
		var res, source,
			that = this;

		_assert( this.isLazy(), "load() requires a lazy node" );
		_assert( forceReload || this.isUndefined(), "Pass forceReload=true to re-load a lazy node" );

		if( this.isLoaded() ){
			this.resetLazy(); 
		}
		
		
		source = this.tree._triggerNodeEvent("lazyLoad", this);
		if( source === false ) { 
			return _getResolvedPromise(this);
		}
		_assert(typeof source !== "boolean", "lazyLoad event must return source in data.result");
		res = this.tree._callHook("nodeLoadChildren", this, source);
		if( this.expanded ) {
			res.always(function(){
				that.render();
			});
		}
		return res;
	},
	/** Expand all parents and optionally scroll into visible area as neccessary.
	 * Promise is resolved, when lazy loading and animations are done.
	 * @param {object} [opts] passed to `setExpanded()`.
	 *     Defaults to {noAnimation: false, noEvents: false, scrollIntoView: true}
	 * @returns {$.Promise}
	 */
	makeVisible: function(opts) {
		var i,
			that = this,
			deferreds = [],
			dfd = new $.Deferred(),
			parents = this.getParentList(false, false),
			len = parents.length,
			effects = !(opts && opts.noAnimation === true),
			scroll = !(opts && opts.scrollIntoView === false);

		
		for(i = len - 1; i >= 0; i--){
			
			deferreds.push(parents[i].setExpanded(true, opts));
		}
		$.when.apply($, deferreds).done(function(){
			
			
			if( scroll ){
				that.scrollIntoView(effects).done(function(){
					
					dfd.resolve();
				});
			} else {
				dfd.resolve();
			}
		});
		return dfd.promise();
	},
	/** Move this node to targetNode.
	 *  @param {FancytreeNode} targetNode
	 *  @param {string} mode <pre>
	 *      'child': append this node as last child of targetNode.
	 *               This is the default. To be compatble with the D'n'd
	 *               hitMode, we also accept 'over'.
	 *      'before': add this node as sibling before targetNode.
	 *      'after': add this node as sibling after targetNode.</pre>
	 *  @param {function} [map] optional callback(FancytreeNode) to allow modifcations
	 */
	moveTo: function(targetNode, mode, map) {
		if(mode === undefined || mode === "over"){
			mode = "child";
		}
		var pos,
			prevParent = this.parent,
			targetParent = (mode === "child") ? targetNode : targetNode.parent;

		if(this === targetNode){
			return;
		}else if( !this.parent  ){
			throw "Cannot move system root";
		}else if( targetParent.isDescendantOf(this) ){
			throw "Cannot move a node to its own descendant";
		}
		
		if( this.parent.children.length === 1 ) {
			this.parent.children = this.parent.lazy ? [] : null;
			this.parent.expanded = false;
		} else {
			pos = $.inArray(this, this.parent.children);
			_assert(pos >= 0);
			this.parent.children.splice(pos, 1);
		}
		




		
		this.parent = targetParent;
		if( targetParent.hasChildren() ) {
			switch(mode) {
			case "child":
				
				targetParent.children.push(this);
				break;
			case "before":
				
				pos = $.inArray(targetNode, targetParent.children);
				_assert(pos >= 0);
				targetParent.children.splice(pos, 0, this);
				break;
			case "after":
				
				pos = $.inArray(targetNode, targetParent.children);
				_assert(pos >= 0);
				targetParent.children.splice(pos+1, 0, this);
				break;
			default:
				throw "Invalid mode " + mode;
			}
		} else {
			targetParent.children = [ this ];
		}
		












		
		if( map ){
			targetNode.visit(map, true);
		}
		
		if( this.tree !== targetNode.tree ) {
			

			this.warn("Cross-tree moveTo is experimantal!");
			this.visit(function(n){
				
				n.tree = targetNode.tree;
			}, true);
		}

		
		
		
		

		
		if( !prevParent.isDescendantOf(targetParent)) {
			prevParent.render();
		}
		if( !targetParent.isDescendantOf(prevParent) && targetParent !== prevParent) {
			targetParent.render();
		}
		
		

/*
		var tree = this.tree;
		var opts = tree.options;
		var pers = tree.persistence;


		

		if ( opts.minExpandLevel >= ftnode.getLevel() ) {

			this.bExpanded = true;
		}

		
		



		
		if( ftnode.bSelected && opts.selectMode==3 ) {
			var p = this;
			while( p ) {
				if( !p.hasSubSel )
					p._setSubSel(true);
				p = p.parent;
			}
		}
		
		if ( tree.bEnableUpdate )
			this.render();

		return ftnode;

*/
	},
	/** Set focus relative to this node and optionally activate.
	 *
	 * @param {number} where The keyCode that would normally trigger this move,
	 *		e.g. `$.ui.keyCode.LEFT` would collapse the node if it
	 *      is expanded or move to the parent oterwise.
	 * @param {boolean} [activate=true]
	 * @returns {$.Promise}
	 */
	navigate: function(where, activate) {
		var i, parents,
			handled = true,
			KC = $.ui.keyCode,
			sib = null;

		
		function _goto(n){
			if( n ){
				n.makeVisible();
				
				if( ! $(n.span).is(":visible") ) {
					n.debug("Navigate: skipping hidden node");
					n.navigate(where, activate);
					return;
				}
				return activate === false ? n.setFocus() : n.setActive();
			}
		}

		switch( where ) {
			case KC.BACKSPACE:
				if( this.parent && this.parent.parent ) {
					_goto(this.parent);
				}
				break;
			case KC.LEFT:
				if( this.expanded ) {
					this.setExpanded(false);
					_goto(this);
				} else if( this.parent && this.parent.parent ) {
					_goto(this.parent);
				}
				break;
			case KC.RIGHT:
				if( !this.expanded && (this.children || this.lazy) ) {
					this.setExpanded();
					_goto(this);
				} else if( this.children && this.children.length ) {
					_goto(this.children[0]);
				}
				break;
			case KC.UP:
				sib = this.getPrevSibling();
				while( sib && sib.expanded && sib.children && sib.children.length ){
					sib = sib.children[sib.children.length - 1];
				}
				if( !sib && this.parent && this.parent.parent ){
					sib = this.parent;
				}
				_goto(sib);
				break;
			case KC.DOWN:
				if( this.expanded && this.children && this.children.length ) {
					sib = this.children[0];
				} else {
					parents = this.getParentList(false, true);
					for(i=parents.length-1; i>=0; i--) {
						sib = parents[i].getNextSibling();
						if( sib ){ break; }
					}
				}
				_goto(sib);
				break;
			default:
				handled = false;
		}
	},
	/**
	 * Remove this node (not allowed for system root).
	 */
	remove: function() {
		return this.parent.removeChild(this);
	},
	/**
	 * Remove childNode from list of direct children.
	 * @param {FancytreeNode} childNode
	 */
	removeChild: function(childNode) {
		return this.tree._callHook("nodeRemoveChild", this, childNode);
	},
	/**
	 * Remove all child nodes and descendents. This converts the node into a leaf.<br>
	 * If this was a lazy node, it is still considered 'loaded'; call node.resetLazy()
	 * in order to trigger lazyLoad on next expand.
	 */
	removeChildren: function() {
		return this.tree._callHook("nodeRemoveChildren", this);
	},
	/**
	 * This method renders and updates all HTML markup that is required
	 * to display this node in its current state.<br>
	 * Note:
	 * <ul>
	 * <li>It should only be neccessary to call this method after the node object
	 *     was modified by direct access to its properties, because the common
	 *     API methods (node.setTitle(), moveTo(), addChildren(), remove(), ...)
	 *     already handle this.
	 * <li> {@link FancytreeNode#renderTitle} and {@link FancytreeNode#renderStatus}
	 *     are implied. If changes are more local, calling only renderTitle() or
	 *     renderStatus() may be sufficient and faster.
	 * <li>If a node was created/removed, node.render() must be called <i>on the parent</i>.
	 * </ul>
	 *
	 * @param {boolean} [force=false] re-render, even if html markup was already created
	 * @param {boolean} [deep=false] also render all descendants, even if parent is collapsed
	 */
	render: function(force, deep) {
		return this.tree._callHook("nodeRender", this, force, deep);
	},
	/** Create HTML markup for the node's outer <span> (expander, checkbox, icon, and title).
	 * @see Fancytree_Hooks#nodeRenderTitle
	 */
	renderTitle: function() {
		return this.tree._callHook("nodeRenderTitle", this);
	},
	/** Update element's CSS classes according to node state.
	 * @see Fancytree_Hooks#nodeRenderStatus
	 */
	renderStatus: function() {
		return this.tree._callHook("nodeRenderStatus", this);
	},
	/**
	 * Remove all children, collapse, and set the lazy-flag, so that the lazyLoad
	 * event is triggered on next expand.
	 */
	resetLazy: function() {
		this.removeChildren();
		this.expanded = false;
		this.lazy = true;
		this.children = undefined;
		this.renderStatus();
	},
	/** Schedule activity for delayed execution (cancel any pending request).
	 *  scheduleAction('cancel') will only cancel a pending request (if any).
	 * @param {string} mode
	 * @param {number} ms
	 */
	scheduleAction: function(mode, ms) {
		if( this.tree.timer ) {
			clearTimeout(this.tree.timer);

		}
		this.tree.timer = null;
		var self = this; 
		switch (mode) {
		case "cancel":
			
			break;
		case "expand":
			this.tree.timer = setTimeout(function(){
				self.tree.debug("setTimeout: trigger expand");
				self.setExpanded(true);
			}, ms);
			break;
		case "activate":
			this.tree.timer = setTimeout(function(){
				self.tree.debug("setTimeout: trigger activate");
				self.setActive(true);
			}, ms);
			break;
		default:
			throw "Invalid mode " + mode;
		}

	},
	/**
	 *
	 * @param {boolean | PlainObject} [effects=false] animation options.
	 * @param {object} [options=null] {topNode: null, effects: ..., parent: ...} this node will remain visible in
	 *     any case, even if `this` is outside the scroll pane.
	 * @returns {$.Promise}
	 */
	scrollIntoView: function(effects, options) {
		if( options !== undefined && _isNode(options) ) {
			this.warn("scrollIntoView() with 'topNode' option is deprecated since 2014-05-08. Use 'options.topNode' instead.");
			options = {topNode: options};
		}
		
		

		var topNodeY, nodeY, horzScrollbarHeight, containerOffsetTop,
			opts = $.extend({
				effects: (effects === true) ? {duration: 200, queue: false} : effects,
				scrollOfs: this.tree.options.scrollOfs,
				scrollParent: this.tree.options.scrollParent || this.tree.$container,
				topNode: null
			}, options),
			dfd = new $.Deferred(),
			that = this,
			nodeHeight = $(this.span).height(),
			$container = $(opts.scrollParent),
			topOfs = opts.scrollOfs.top || 0,
			bottomOfs = opts.scrollOfs.bottom || 0,
			containerHeight = $container.height(),
			scrollTop = $container.scrollTop(),
			$animateTarget = $container,
			isParentWindow = $container[0] === window,
			topNode = opts.topNode || null,
			newScrollTop = null;

		
		_assert($(this.span).is(":visible"), "scrollIntoView node is invisible"); 

		if( isParentWindow ) {
			nodeY = $(this.span).offset().top;
			topNodeY = topNode ? $(topNode.span).offset().top : 0;
			$animateTarget = $("html,body");

		} else {
			_assert($container[0] !== document && $container[0] !== document.body, "scrollParent should be an simple element or `window`, not document or body.");

			containerOffsetTop = $container.offset().top,
			nodeY = $(this.span).offset().top - containerOffsetTop + scrollTop; 
			topNodeY = topNode ? $(topNode.span).offset().top - containerOffsetTop  + scrollTop : 0;
			horzScrollbarHeight = Math.max(0, ($container.innerHeight() - $container[0].clientHeight));
			containerHeight -= horzScrollbarHeight;
		}

		
		if( nodeY < (scrollTop + topOfs) ){
			
			newScrollTop = nodeY - topOfs;
			

		}else if((nodeY + nodeHeight) > (scrollTop + containerHeight - bottomOfs)){
			newScrollTop = nodeY + nodeHeight - containerHeight + bottomOfs;
			
			
			
			if(topNode){
				_assert($(topNode.span).is(":visible"));
				if( topNodeY < newScrollTop ){
					newScrollTop = topNodeY - topOfs;
					
				}
			}
		}

		if(newScrollTop !== null){
			
			if(opts.effects){
				opts.effects.complete = function(){
					dfd.resolveWith(that);
				};
				$animateTarget.stop(true).animate({
					scrollTop: newScrollTop
				}, opts.effects);
			}else{
				$animateTarget[0].scrollTop = newScrollTop;
				dfd.resolveWith(this);
			}
		}else{
			dfd.resolveWith(this);
		}
		return dfd.promise();
	},

	/**Activate this node.
	 * @param {boolean} [flag=true] pass false to deactivate
	 * @param {object} [opts] additional options. Defaults to {noEvents: false}
	 */
	setActive: function(flag, opts){
		return this.tree._callHook("nodeSetActive", this, flag, opts);
	},
	/**Expand or collapse this node. Promise is resolved, when lazy loading and animations are done.
	 * @param {boolean} [flag=true] pass false to collapse
	 * @param {object} [opts] additional options. Defaults to {noAnimation: false, noEvents: false}
	 * @returns {$.Promise}
	 */
	setExpanded: function(flag, opts){
		return this.tree._callHook("nodeSetExpanded", this, flag, opts);
	},
	/**Set keyboard focus to this node.
	 * @param {boolean} [flag=true] pass false to blur
	 * @see Fancytree#setFocus
	 */
	setFocus: function(flag){
		return this.tree._callHook("nodeSetFocus", this, flag);
	},
	
	/**Select this node, i.e. check the checkbox.
	 * @param {boolean} [flag=true] pass false to deselect
	 */
	setSelected: function(flag){
		return this.tree._callHook("nodeSetSelected", this, flag);
	},
	/**Rename this node.
	 * @param {string} title
	 */
	setTitle: function(title){
		this.title = title;
		this.renderTitle();
	},
	/**Sort child list by title.
	 * @param {function} [cmp] custom compare function(a, b) that returns -1, 0, or 1 (defaults to sort by title).
	 * @param {boolean} [deep=false] pass true to sort all descendant nodes
	 */
	sortChildren: function(cmp, deep) {
		var i,l,
			cl = this.children;

		if( !cl ){
			return;
		}
		cmp = cmp || function(a, b) {
			var x = a.title.toLowerCase(),
				y = b.title.toLowerCase();
			return x === y ? 0 : x > y ? 1 : -1;
			};
		cl.sort(cmp);
		if( deep ){
			for(i=0, l=cl.length; i<l; i++){
				if( cl[i].children ){
					cl[i].sortChildren(cmp, "$norender$");
				}
			}
		}
		if( deep !== "$norender$" ){
			this.render();
		}
	},
	/** Convert node (or whole branch) into a plain object.
	 *
	 * The result is compatible with node.addChildren().
	 *
	 * @param {boolean} recursive
	 * @param {function} callback callback(dict) is called for every node, in order to allow modifications
	 * @returns {NodeData}
	 */
	toDict: function(recursive, callback) {
		var i, l, node,
			dict = {},
			self = this;

		$.each(NODE_ATTRS, function(i, a){
			if(self[a] || self[a] === false){
				dict[a] = self[a];
			}
		});
		if(!$.isEmptyObject(this.data)){
			dict.data = $.extend({}, this.data);
			if($.isEmptyObject(dict.data)){
				delete dict.data;
			}
		}
		if( callback ){
			callback(dict);
		}
		if( recursive ) {
			if(this.hasChildren()){
				dict.children = [];
				for(i=0, l=this.children.length; i<l; i++ ){
					node = this.children[i];
					if( !node.isStatusNode() ){
						dict.children.push(node.toDict(true, callback));
					}
				}
			}else{

			}
		}
		return dict;
	},
	
	toggleExpanded: function(){
		return this.tree._callHook("nodeToggleExpanded", this);
	},
	
	toggleSelected: function(){
		return this.tree._callHook("nodeToggleSelected", this);
	},
	toString: function() {
		return "<FancytreeNode(#" + this.key + ", '" + this.title + "')>";
	},
	/** Call fn(node) for all child nodes.<br>
	 * Stop iteration, if fn() returns false. Skip current branch, if fn() returns "skip".<br>
	 * Return false if iteration was stopped.
	 *
	 * @param {function} fn the callback function.
	 *     Return false to stop iteration, return "skip" to skip this node and children only.
	 * @param {boolean} [includeSelf=false]
	 * @returns {boolean}
	 */
	visit: function(fn, includeSelf) {
		var i, l,
			res = true,
			children = this.children;

		if( includeSelf === true ) {
			res = fn(this);
			if( res === false || res === "skip" ){
				return res;
			}
		}
		if(children){
			for(i=0, l=children.length; i<l; i++){
				res = children[i].visit(fn, true);
				if( res === false ){
					break;
				}
			}
		}
		return res;
	},
	/** Call fn(node) for all parent nodes, bottom-up, including invisible system root.<br>
	 * Stop iteration, if fn() returns false.<br>
	 * Return false if iteration was stopped.
	 *
	 * @param {function} fn the callback function.
	 *     Return false to stop iteration, return "skip" to skip this node and children only.
	 * @param {boolean} [includeSelf=false]
	 * @returns {boolean}
	 */
	visitParents: function(fn, includeSelf) {
		
		if(includeSelf && fn(this) === false){
			return false;
		}
		var p = this.parent;
		while( p ) {
			if(fn(p) === false){
				return false;
			}
			p = p.parent;
		}
		return true;
	},
	/** Write warning to browser console (prepending node info)
	 *
	 * @param {*} msg string or object or array of such
	 */
	warn: function(msg){
		Array.prototype.unshift.call(arguments, this.toString());
		consoleApply("warn", arguments);
	}
};



/**
 * Construct a new tree object.
 *
 * @class Fancytree
 * @classdesc The controller behind a fancytree.
 * This class also contains 'hook methods': see {@link Fancytree_Hooks}.
 *
 * @param {Widget} widget
 *
 * @property {FancytreeOptions} options
 * @property {FancytreeNode} rootNode
 * @property {FancytreeNode} activeNode
 * @property {FancytreeNode} focusNode
 * @property {jQueryObject} $div
 * @property {object} widget
 * @property {object} ext
 * @property {object} data
 * @property {object} options
 * @property {string} _id
 * @property {string} statusClassPropName
 * @property {string} ariaPropName
 * @property {string} nodeContainerAttrName
 * @property {string} $container
 * @property {FancytreeNode} lastSelectedNode
 */
function Fancytree(widget) {
	this.widget = widget;
	this.$div = widget.element;
	this.options = widget.options;
	if( this.options && $.isFunction(this.options.lazyload) ) {
		if( ! $.isFunction(this.options.lazyLoad ) ) {
			this.options.lazyLoad = function() {
				FT.warn("The 'lazyload' event is deprecated since 2014-02-25. Use 'lazyLoad' (with uppercase L) instead.");
				widget.options.lazyload.apply(this, arguments);
			};
		}
	}
	this.ext = {}; 
	
	this.data = _getElementDataAsDict(this.$div);
	this._id = $.ui.fancytree._nextId++;
	this._ns = ".fancytree-" + this._id; 
	this.activeNode = null;
	this.focusNode = null;
	this._hasFocus = null;
	this.lastSelectedNode = null;
	this.systemFocusElement = null;

	this.statusClassPropName = "span";
	this.ariaPropName = "li";
	this.nodeContainerAttrName = "li";

	
	this.$div.find(">ul.fancytree-container").remove();

	
	var fakeParent = { tree: this },
		$ul;
	this.rootNode = new FancytreeNode(fakeParent, {
		title: "root",
		key: "root_" + this._id,
		children: null,
		expanded: true
	});
	this.rootNode.parent = null;

	
	$ul = $("<ul>", {
		"class": "ui-fancytree fancytree-container"
	}).appendTo(this.$div);
	this.$container = $ul;
	this.rootNode.ul = $ul[0];

	if(this.options.debugLevel == null){
		this.options.debugLevel = FT.debugLevel;
	}
	
	
	this.$container.attr("tabindex", this.options.tabbable ? "0" : "-1");
	if(this.options.aria){
		this.$container
			.attr("role", "tree")
			.attr("aria-multiselectable", true);
	}
}


Fancytree.prototype = {
	
	_makeHookContext: function(obj, originalEvent, extra) {
		var ctx, tree;
		if(obj.node !== undefined){
			
			if(originalEvent && obj.originalEvent !== originalEvent){
				$.error("invalid args");
			}
			ctx = obj;
		}else if(obj.tree){
			
			tree = obj.tree;
			ctx = { node: obj, tree: tree, widget: tree.widget, options: tree.widget.options, originalEvent: originalEvent };
		}else if(obj.widget){
			
			ctx = { node: null, tree: obj, widget: obj.widget, options: obj.widget.options, originalEvent: originalEvent };
		}else{
			$.error("invalid args");
		}
		if(extra){
			$.extend(ctx, extra);
		}
		return ctx;
	},
	
	_callHook: function(funcName, contextObject, _extraArgs) {
		var ctx = this._makeHookContext(contextObject),
			fn = this[funcName],
			args = Array.prototype.slice.call(arguments, 2);
		if(!$.isFunction(fn)){
			$.error("_callHook('" + funcName + "') is not a function");
		}
		args.unshift(ctx);

		return fn.apply(this, args);
	},
	
	_requireExtension: function(name, required, before, message) {
		before = !!before;
		var thisName = this._local.name,
			extList = this.options.extensions,
			isBefore = $.inArray(name, extList) < $.inArray(thisName, extList),
			isMissing = required && this.ext[name] == null,
			badOrder = !isMissing && before != null && (before !== isBefore);

		_assert(thisName && thisName !== name);

		if( isMissing || badOrder ){
			if( !message ){
				if( isMissing || required ){
					message = "'" + thisName + "' extension requires '" + name + "'";
					if( badOrder ){
						message += " to be registered " + (before ? "before" : "after") + " itself";
					}
				}else{
					message = "If used together, `" + name + "` must be registered " + (before ? "before" : "after") + " `" + thisName + "`";
				}
			}
			$.error(message);
			return false;
		}
		return true;
	},
	/** Activate node with a given key and fire focus and activate events.
	 *
	 * A prevously activated node will be deactivated.
	 * If activeVisible option is set, all parents will be expanded as necessary.
	 * Pass key = false, to deactivate the current node only.
	 * @param {string} key
	 * @returns {FancytreeNode} activated node (null, if not found)
	 */
	activateKey: function(key) {
		var node = this.getNodeByKey(key);
		if(node){
			node.setActive();
		}else if(this.activeNode){
			this.activeNode.setActive(false);
		}
		return node;
	},
	/** (experimental)
	 *
	 * @param {Array} patchList array of [key, NodePatch] arrays
	 * @returns {$.Promise} resolved, when all patches have been applied
	 * @see TreePatch
	 */
	applyPatch: function(patchList) {
		var dfd, i, p2, key, patch, node,
			patchCount = patchList.length,
			deferredList = [];

		for(i=0; i<patchCount; i++){
			p2 = patchList[i];
			_assert(p2.length === 2, "patchList must be an array of length-2-arrays");
			key = p2[0];
			patch = p2[1];
			node = (key === null) ? this.rootNode : this.getNodeByKey(key);
			if(node){
				dfd = new $.Deferred();
				deferredList.push(dfd);
				node.applyPatch(patch).always(_makeResolveFunc(dfd, node));
			}else{
				this.warn("could not find node with key '" + key + "'");
			}
		}
		
		return $.when.apply($, deferredList).promise();
	},
	
   /** Return the number of nodes.
	* @returns {integer}
	*/
	count: function() {
		return this.rootNode.countChildren();
	},
	/** Write to browser console if debugLevel >= 2 (prepending tree name)
	 *
	 * @param {*} msg string or object or array of such
	 */
	debug: function(msg){
		if( this.options.debugLevel >= 2 ) {
			Array.prototype.unshift.call(arguments, this.toString());
			consoleApply("debug", arguments);
		}
	},
	
	
	
	
	/**
	 * Generate INPUT elements that can be submitted with html forms.
	 *
	 * In selectMode 3 only the topmost selected nodes are considered.
	 *
	 * @param {boolean | string} [selected=true]
	 * @param {boolean | string} [active=true]
	 */
	generateFormElements: function(selected, active) {
		
		var nodeList,
			selectedName = (selected !== false) ? "ft_" + this._id + "[]" : selected,
			activeName = (active !== false) ? "ft_" + this._id + "_active" : active,
			id = "fancytree_result_" + this._id,
			$result = $("#" + id);

		if($result.length){
			$result.empty();
		}else{
			$result = $("<div>", {
				id: id
			}).hide().insertAfter(this.$container);
		}
		if(selectedName){
			nodeList = this.getSelectedNodes( this.options.selectMode === 3 );
			$.each(nodeList, function(idx, node){
				$result.append($("<input>", {
					type: "checkbox",
					name: selectedName,
					value: node.key,
					checked: true
				}));
			});
		}
		if(activeName && this.activeNode){
			$result.append($("<input>", {
				type: "radio",
				name: activeName,
				value: this.activeNode.key,
				checked: true
			}));
		}
	},
	/**
	 * Return the currently active node or null.
	 * @returns {FancytreeNode}
	 */
	getActiveNode: function() {
		return this.activeNode;
	},
	/** Return the first top level node if any (not the invisible root node).
	 * @returns {FancytreeNode | null}
	 */
	getFirstChild: function() {
		return this.rootNode.getFirstChild();
	},
	/**
	 * Return node that has keyboard focus.
	 * @param {boolean} [ifTreeHasFocus=false] (not yet implemented)
	 * @returns {FancytreeNode}
	 */
	getFocusNode: function(ifTreeHasFocus) {
		
		return this.focusNode;
	},
	/**
	 * Return node with a given key or null if not found.
	 * @param {string} key
	 * @param {FancytreeNode} [searchRoot] only search below this node
	 * @returns {FancytreeNode | null}
	 */
	getNodeByKey: function(key, searchRoot) {
		
		
		var el, match;
		if(!searchRoot){
			el = document.getElementById(this.options.idPrefix + key);
			if( el ){
				return el.ftnode ? el.ftnode : null;
			}
		}
		
		
		
		searchRoot = searchRoot || this.rootNode;
		match = null;
		searchRoot.visit(function(node){

			if(node.key === key) {
				match = node;
				return false;
			}
		}, true);
		return match;
	},
	
	/**
	 * Return an array of selected nodes.
	 * @param {boolean} [stopOnParents=false] only return the topmost selected
	 *     node (useful with selectMode 3)
	 * @returns {FancytreeNode[]}
	 */
	getSelectedNodes: function(stopOnParents) {
		var nodeList = [];
		this.rootNode.visit(function(node){
			if( node.selected ) {
				nodeList.push(node);
				if( stopOnParents === true ){
					return "skip"; 
				}
			}
		});
		return nodeList;
	},
	/** Return true if the tree control has keyboard focus
	 * @returns {boolean}
	 */
	hasFocus: function(){
		return !!this._hasFocus;
	},
	/** Write to browser console if debugLevel >= 1 (prepending tree name)
	 * @param {*} msg string or object or array of such
	 */
	info: function(msg){
		if( this.options.debugLevel >= 1 ) {
			Array.prototype.unshift.call(arguments, this.toString());
			consoleApply("info", arguments);
		}
	},
/*
	TODO: isInitializing: function() {
		return ( this.phase=="init" || this.phase=="postInit" );
	},
	TODO: isReloading: function() {
		return ( this.phase=="init" || this.phase=="postInit" ) && this.options.persist && this.persistence.cookiesFound;
	},
	TODO: isUserEvent: function() {
		return ( this.phase=="userEvent" );
	},
*/

	/**
	 * Make sure that a node with a given ID is loaded, by traversing - and
	 * loading - its parents. This method is ment for lazy hierarchies.
	 * A callback is executed for every node as we go.
	 * @example
	 * tree.loadKeyPath("/_3/_23/_26/_27", function(node, status){
	 *   if(status === "loaded") {
	 *     console.log("loaded intermiediate node " + node);
	 *   }else if(status === "ok") {
	 *     node.activate();
	 *   }
	 * });
	 *
	 * @param {string | string[]} keyPathList one or more key paths (e.g. '/3/2_1/7')
	 * @param {function} callback callback(node, status) is called for every visited node ('loading', 'loaded', 'ok', 'error')
	 * @returns {$.Promise}
	 */
	loadKeyPath: function(keyPathList, callback, _rootNode) {
		var deferredList, dfd, i, path, key, loadMap, node, segList,
			root = _rootNode || this.rootNode,
			sep = this.options.keyPathSeparator,
			self = this;

		if(!$.isArray(keyPathList)){
			keyPathList = [keyPathList];
		}
		
		
		loadMap = {};

		for(i=0; i<keyPathList.length; i++){
			path = keyPathList[i];
			
			if(path.charAt(0) === sep){
				path = path.substr(1);
			}
			
			segList = path.split(sep);
			while(segList.length){
				key = segList.shift();

				node = root._findDirectChild(key);
				if(!node){
					this.warn("loadKeyPath: key not found: " + key + " (parent: " + root + ")");
					callback.call(this, key, "error");
					break;
				}else if(segList.length === 0){
					callback.call(this, node, "ok");
					break;
				}else if(!node.lazy || (node.hasChildren() !== undefined )){
					callback.call(this, node, "loaded");
					root = node;
				}else{
					callback.call(this, node, "loaded");

					if(loadMap[key]){
						loadMap[key].push(segList.join(sep));
					}else{
						loadMap[key] = [segList.join(sep)];
					}
					break;
				}
			}
		}

		
		deferredList = [];
		
		function __lazyload(key, node, dfd){
			callback.call(self, node, "loading");
			node.load().done(function(){
				self.loadKeyPath.call(self, loadMap[key], callback, node).always(_makeResolveFunc(dfd, self));
			}).fail(function(errMsg){
				self.warn("loadKeyPath: error loading: " + key + " (parent: " + root + ")");
				callback.call(self, node, "error");
				dfd.reject();
			});
		}
		for(key in loadMap){
			node = root._findDirectChild(key);

			dfd = new $.Deferred();
			deferredList.push(dfd);
			__lazyload(key, node, dfd);
		}
		
		return $.when.apply($, deferredList).promise();
	},
	
	reactivate: function(setFocus) {
		var node = this.activeNode;
		if( node ) {
			this.activeNode = null; 
			node.setActive();
			if( setFocus ){
				node.setFocus();
			}
		}
	},
	/** Reload tree from source and return a promise.
	 * @param [source] optional new source (defaults to initial source data)
	 * @returns {$.Promise}
	 */
	reload: function(source) {
		this._callHook("treeClear", this);
		return this._callHook("treeLoad", this, source);
	},
	/**Render tree (i.e. create DOM elements for all top-level nodes).
	 * @param {boolean} [force=false] create DOM elemnts, even is parent is collapsed
	 * @param {boolean} [deep=false]
	 */
	render: function(force, deep) {
		return this.rootNode.render(force, deep);
	},
	
	
	/**
	 * @param {boolean} [flag=true]
	 */
	setFocus: function(flag) {
		return this._callHook("treeSetFocus", this, flag);
	},
	/**
	 * Return all nodes as nested list of {@link NodeData}.
	 *
	 * @param {boolean} [includeRoot=false] Returns the hidden system root node (and its children)
	 * @param {function} [callback(node)] Called for every node
	 * @returns {Array | object}
	 * @see FancytreeNode#toDict
	 */
	toDict: function(includeRoot, callback){
		var res = this.rootNode.toDict(true, callback);
		return includeRoot ? res : res.children;
	},
	
	toString: function(){
		return "<Fancytree(#" + this._id + ")>";
	},
	
	_triggerNodeEvent: function(type, node, originalEvent, extra) {

		var ctx = this._makeHookContext(node, originalEvent, extra),
			res = this.widget._trigger(type, originalEvent, ctx);
		if(res !== false && ctx.result !== undefined){
			return ctx.result;
		}
		return res;
	},
	
	_triggerTreeEvent: function(type, originalEvent) {

		var ctx = this._makeHookContext(this, originalEvent),
			res = this.widget._trigger(type, originalEvent, ctx);

		if(res !== false && ctx.result !== undefined){
			return ctx.result;
		}
		return res;
	},
	/** Call fn(node) for all nodes.
	 *
	 * @param {function} fn the callback function.
	 *     Return false to stop iteration, return "skip" to skip this node and children only.
	 * @returns {boolean} false, if the iterator was stopped.
	 */
	visit: function(fn) {
		return this.rootNode.visit(fn, false);
	},
	/** Write warning to browser console (prepending tree info)
	 *
	 * @param {*} msg string or object or array of such
	 */
	warn: function(msg){
		Array.prototype.unshift.call(arguments, this.toString());
		consoleApply("warn", arguments);
	}
};

/**
 * These additional methods of the {@link Fancytree} class are 'hook functions'
 * that can be used and overloaded by extensions.
 * (See <a href="https://github.com/mar10/fancytree/wiki/TutorialExtensions">writing extensions</a>.)
 * @mixin Fancytree_Hooks
 */
$.extend(Fancytree.prototype,
	
	{
	/** Default handling for mouse click events.
	 *
	 * @param {EventData} ctx
	 */
	nodeClick: function(ctx) {

		var activate, expand,
			event = ctx.originalEvent,
			targetType = ctx.targetType,
			node = ctx.node;

		
		
		if( targetType === "expander" ) {
			
			this._callHook("nodeToggleExpanded", ctx);

		} else if( targetType === "checkbox" ) {
			
			this._callHook("nodeToggleSelected", ctx);
			this._callHook("nodeSetFocus", ctx, true); 
		} else {
			
			expand = false;
			activate = true;
			if( node.folder ) {
				switch( ctx.options.clickFolderMode ) {
				case 2: 
					expand = true;
					activate = false;
					break;
				case 3: 
					activate = true;
					expand = true;
					break;
				
				}
			}
			if( activate ) {
				this.nodeSetFocus(ctx);
				this._callHook("nodeSetActive", ctx, true);
			}
			if( expand ) {
				if(!activate){

				}

				this._callHook("nodeToggleExpanded", ctx);
			}
		}
		
		if(event.target.localName === "a" && event.target.className === "fancytree-title"){
			event.preventDefault();
		}
		
	},
	/** Collapse all other  children of same parent.
	 *
	 * @param {EventData} ctx
	 * @param {object} callOpts
	 */
	nodeCollapseSiblings: function(ctx, callOpts) {
		
		var ac, i, l,
			node = ctx.node;

		if( node.parent ){
			ac = node.parent.children;
			for (i=0, l=ac.length; i<l; i++) {
				if ( ac[i] !== node && ac[i].expanded ){
					this._callHook("nodeSetExpanded", ac[i], false, callOpts);
				}
			}
		}
	},
	/** Default handling for mouse douleclick events.
	 * @param {EventData} ctx
	 */
	nodeDblclick: function(ctx) {
		
		if( ctx.targetType === "title" && ctx.options.clickFolderMode === 4) {


			this._callHook("nodeToggleExpanded", ctx);
		}
		
		if( ctx.targetType === "title" ) {
			ctx.originalEvent.preventDefault();
		}
	},
	/** Default handling for mouse keydown events.
	 *
	 * NOTE: this may be called with node == null if tree (but no node) has focus.
	 * @param {EventData} ctx
	 */
	nodeKeydown: function(ctx) {
		
		var res,
			event = ctx.originalEvent,
			node = ctx.node,
			tree = ctx.tree,
			opts = ctx.options,
			handled = true,
			activate = !(event.ctrlKey || !opts.autoActivate ),
			KC = $.ui.keyCode;



		
		if( !node ){
			this.rootNode.getFirstChild().setFocus();
			node = ctx.node = this.focusNode;
			node.debug("Keydown force focus on first node");
		}

		switch( event.which ) {
			
			case KC.NUMPAD_ADD:
			case 187: 
				tree.nodeSetExpanded(ctx, true);
				break;
			case KC.NUMPAD_SUBTRACT: 
			case 189: 
				tree.nodeSetExpanded(ctx, false);
				break;
			case KC.SPACE:
				if(opts.checkbox){
					tree.nodeToggleSelected(ctx);
				}else{
					tree.nodeSetActive(ctx, true);
				}
				break;
			case KC.ENTER:
				tree.nodeSetActive(ctx, true);
				break;
			case KC.BACKSPACE:
			case KC.LEFT:
			case KC.RIGHT:
			case KC.UP:
			case KC.DOWN:
				res = node.navigate(event.which, activate);
				break;
			default:
				handled = false;
		}
		if(handled){
			event.preventDefault();
		}
	},



	
	
	


	
	
	
	
	/** Load child nodes (async).
	 *
	 * @param {EventData} ctx
	 * @param {object[]|object|string|$.Promise|function} source
	 * @returns {$.Promise} The deferred will be resolved as soon as the (ajax)
	 *     data was rendered.
	 */
	nodeLoadChildren: function(ctx, source) {
		var ajax, delay,
			tree = ctx.tree,
			node = ctx.node;

		if($.isFunction(source)){
			source = source();
		}
		
		if(source.url){
			
			ajax = $.extend({}, ctx.options.ajax, source);
			if(ajax.debugDelay){
				
				delay = ajax.debugDelay;
				if($.isArray(delay)){ 
					delay = delay[0] + Math.random() * (delay[1] - delay[0]);
				}

				node.debug("nodeLoadChildren waiting debug delay " + Math.round(delay) + "ms");
				ajax.debugDelay = false;
				source = $.Deferred(function (dfd) {
					setTimeout(function () {
						$.ajax(ajax)
							.done(function () {	dfd.resolveWith(this, arguments); })
							.fail(function () {	dfd.rejectWith(this, arguments); });
					}, delay);
				});
			}else{
				source = $.ajax(ajax);
			}

			
			
			source = source.pipe(function (data, textStatus, jqXHR) {
				var res;
				if(typeof data === "string"){
					$.error("Ajax request returned a string (did you get the JSON dataType wrong?).");
				}
				
				
				if( ctx.options.postProcess ){
					res = tree._triggerNodeEvent("postProcess", ctx, ctx.originalEvent, {response: data, dataType: this.dataType});
					data = $.isArray(res) ? res : data;
				} else if (data && data.hasOwnProperty("d") && ctx.options.enableAspx ) {
					
					data = (typeof data.d === "string") ? $.parseJSON(data.d) : data.d;
				}
				return data;
			}, function (jqXHR, textStatus, errorThrown) {
				return tree._makeHookContext(node, null, {
					error: jqXHR,
					args: Array.prototype.slice.call(arguments),
					message: errorThrown,
					details: jqXHR.status + ": " + errorThrown
				});
			});
		}

		if($.isFunction(source.promise)){
			
			_assert(!node.isLoading());
			
			tree.nodeSetStatus(ctx, "loading");

			source.done(function () {
				tree.nodeSetStatus(ctx, "ok");
			}).fail(function(error){
				var ctxErr;
				if (error.node && error.error && error.message) {
					
					ctxErr = error;
				} else {
					ctxErr = tree._makeHookContext(node, null, {
						error: error, 
						args: Array.prototype.slice.call(arguments),
						message: error ? (error.message || error.toString()) : ""
					});
				}
				tree._triggerNodeEvent("loaderror", ctxErr, null);
				tree.nodeSetStatus(ctx, "error", ctxErr.message, ctxErr.details);
			});
		}
		
		return $.when(source).done(function(children){
			var metaData;

			if( $.isPlainObject(children) ){
				
				
				_assert($.isArray(children.children), "source must contain (or be) an array of children");
				_assert(node.isRoot(), "source may only be an object for root nodes");
				metaData = children;
				children = children.children;
				delete metaData.children;
				$.extend(tree.data, metaData);
			}
			_assert($.isArray(children), "expected array of children");
			node._setChildren(children);
			
			
			tree._triggerNodeEvent("loadChildren", node);
			
		
		
		});
	},
	
	nodeLoadKeyPath: function(ctx, keyPathList) {
		
		
	},
	/**
	 * Remove a single direct child of ctx.node.
	 * @param {EventData} ctx
	 * @param {FancytreeNode} childNode dircect child of ctx.node
	 */
	nodeRemoveChild: function(ctx, childNode) {
		var idx,
			node = ctx.node,
			opts = ctx.options,
			subCtx = $.extend({}, ctx, {node: childNode}),
			children = node.children;

		

		if( children.length === 1 ) {
			_assert(childNode === children[0]);
			return this.nodeRemoveChildren(ctx);
		}
		if( this.activeNode && (childNode === this.activeNode || this.activeNode.isDescendantOf(childNode))){
			this.activeNode.setActive(false); 
		}
		if( this.focusNode && (childNode === this.focusNode || this.focusNode.isDescendantOf(childNode))){
			this.focusNode = null;
		}
		
		this.nodeRemoveMarkup(subCtx);
		this.nodeRemoveChildren(subCtx);
		idx = $.inArray(childNode, children);
		_assert(idx >= 0);
		
		childNode.visit(function(n){
			n.parent = null;
		}, true);
		this._callHook("treeRegisterNode", this, false, childNode);
		if ( opts.removeNode ){
			opts.removeNode.call(ctx.tree, {type: "removeNode"}, subCtx);
		}
		
		children.splice(idx, 1);
	},
	/**Remove HTML markup for all descendents of ctx.node.
	 * @param {EventData} ctx
	 */
	nodeRemoveChildMarkup: function(ctx) {
		var node = ctx.node;

		
		
		if(node.ul){
			if( node.isRoot() ) {
				$(node.ul).empty();
			} else {
				$(node.ul).remove();
				node.ul = null;
			}
			node.visit(function(n){
				n.li = n.ul = null;
			});
		}
	},
	/**Remove all descendants of ctx.node.
	* @param {EventData} ctx
	*/
	nodeRemoveChildren: function(ctx) {
		var subCtx,
			tree = ctx.tree,
			node = ctx.node,
			children = node.children,
			opts = ctx.options;

		
		if(!children){
			return;
		}
		if( this.activeNode && this.activeNode.isDescendantOf(node)){
			this.activeNode.setActive(false); 
		}
		if( this.focusNode && this.focusNode.isDescendantOf(node)){
			this.focusNode = null;
		}
		
		this.nodeRemoveChildMarkup(ctx);
		
		
		subCtx = $.extend({}, ctx);
		node.visit(function(n){
			n.parent = null;
			tree._callHook("treeRegisterNode", tree, false, n);
			if ( opts.removeNode ){
				subCtx.node = n;
				opts.removeNode.call(ctx.tree, {type: "removeNode"}, subCtx);
			}
		});
		if( node.lazy ){
			
			node.children = [];
		} else{
			node.children = null;
		}
		this.nodeRenderStatus(ctx);
	},
	/**Remove HTML markup for ctx.node and all its descendents.
	 * @param {EventData} ctx
	 */
	nodeRemoveMarkup: function(ctx) {
		var node = ctx.node;
		
		
		if(node.li){
			$(node.li).remove();
			node.li = null;
		}
		this.nodeRemoveChildMarkup(ctx);
	},
	/**
	 * Create `&lt;li>&lt;span>..&lt;/span> .. &lt;/li>` tags for this node.
	 *
	 * This method takes care that all HTML markup is created that is required
	 * to display this node in it's current state.
	 *
	 * Call this method to create new nodes, or after the strucuture
	 * was changed (e.g. after moving this node or adding/removing children)
	 * nodeRenderTitle() and nodeRenderStatus() are implied.
	 *
	 * Note: if a node was created/removed, nodeRender() must be called for the
	 *       parent.
	 * <code>
	 * <li id='KEY' ftnode=NODE>
	 *     <span class='fancytree-node fancytree-expanded fancytree-has-children fancytree-lastsib fancytree-exp-el fancytree-ico-e'>
	 *         <span class="fancytree-expander"></span>
	 *         <span class="fancytree-checkbox"></span> 
	 *         <span class="fancytree-icon"></span>
	 *         <a href="#" class="fancytree-title"> Node 1 </a>
	 *     </span>
	 *     <ul> 
	 *         <li id='KEY' ftnode=NODE> child1 ... </li>
	 *         <li id='KEY' ftnode=NODE> child2 ... </li>
	 *     </ul>
	 * </li>
	 * </code>
	 *
	 * @param {EventData} ctx
	 * @param {boolean} [force=false] re-render, even if html markup was already created
	 * @param {boolean} [deep=false] also render all descendants, even if parent is collapsed
	 * @param {boolean} [collapsed=false] force root node to be collapsed, so we can apply animated expand later
	 */
	nodeRender: function(ctx, force, deep, collapsed, _recursive) {
		
		var childLI, childNode1, childNode2, i, l, next, subCtx,
			node = ctx.node,
			tree = ctx.tree,
			opts = ctx.options,
			aria = opts.aria,
			firstTime = false,
			parent = node.parent,
			isRootNode = !parent,
			children = node.children;
		

		if( ! isRootNode && ! parent.ul ) {
			
			return;
		}
		_assert(isRootNode || parent.ul, "parent UL must exist");









		
		if( !isRootNode ){
			
			if(node.li && (force || (node.li.parentNode !== node.parent.ul) ) ){
				if(node.li.parentNode !== node.parent.ul){

					this.warn("unlink " + node + " (must be child of " + node.parent + ")");
				}

				this.nodeRemoveMarkup(ctx);
			}
			

			if( !node.li ) {

				firstTime = true;
				node.li = document.createElement("li");
				node.li.ftnode = node;
				if(aria){
					



				}
				if( node.key && opts.generateIds ){
					node.li.id = opts.idPrefix + node.key;
				}
				node.span = document.createElement("span");
				node.span.className = "fancytree-node";
				if(aria){
					$(node.span).attr("aria-labelledby", "ftal_" + node.key);
				}
				node.li.appendChild(node.span);

				
				this.nodeRenderTitle(ctx);

				
				if ( opts.createNode ){
					opts.createNode.call(tree, {type: "createNode"}, ctx);
				}
			}else{

				this.nodeRenderStatus(ctx);
			}
			
			if ( opts.renderNode ){
				opts.renderNode.call(tree, {type: "renderNode"}, ctx);
			}
		}

		
		if( children ){
			if( isRootNode || node.expanded || deep === true ) {
				
				if( !node.ul ){
					node.ul = document.createElement("ul");
					if((collapsed === true && !_recursive) || !node.expanded){
						
						node.ul.style.display = "none";
					}
					if(aria){
						$(node.ul).attr("role", "group");
					}
					if ( node.li ) { 
						node.li.appendChild(node.ul);
					} else {
						node.tree.$div.append(node.ul);
					}
				}
				
				for(i=0, l=children.length; i<l; i++) {
					subCtx = $.extend({}, ctx, {node: children[i]});
					this.nodeRender(subCtx, force, deep, false, true);
				}
				
				childLI = node.ul.firstChild;
				while( childLI ){
					childNode2 = childLI.ftnode;
					if( childNode2 && childNode2.parent !== node ) {
						node.debug("_fixParent: remove missing " + childNode2, childLI);
						next = childLI.nextSibling;
						childLI.parentNode.removeChild(childLI);
						childLI = next;
					}else{
						childLI = childLI.nextSibling;
					}
				}
				
				childLI = node.ul.firstChild;
				for(i=0, l=children.length-1; i<l; i++) {
					childNode1 = children[i];
					childNode2 = childLI.ftnode;
					if( childNode1 !== childNode2 ) {
						
						node.ul.insertBefore(childNode1.li, childNode2.li);
					} else {
						childLI = childLI.nextSibling;
					}
				}
			}
		}else{
			
			if( node.ul ){

				this.warn("remove child markup for " + node);
				this.nodeRemoveChildMarkup(ctx);
			}
		}
		if( !isRootNode ){
			
			
			
			if(firstTime){
				parent.ul.appendChild(node.li);
			}
		}
	},
	/** Create HTML for the node's outer <span> (expander, checkbox, icon, and title).
	 *
	 * nodeRenderStatus() is implied.
	 * @param {EventData} ctx
	 * @param {string} [title] optinal new title
	 */
	nodeRenderTitle: function(ctx, title) {
		
		var id, imageSrc, nodeTitle, role, tabindex, tooltip,
			node = ctx.node,
			tree = ctx.tree,
			opts = ctx.options,
			aria = opts.aria,
			level = node.getLevel(),
			ares = [],
			icon = node.data.icon;

		if(title !== undefined){
			node.title = title;
		}
		if(!node.span){
			
			
			return;
		}
		
		
		if( level < opts.minExpandLevel ) {
			if(level > 1){
				if(aria){
					ares.push("<span role='button' class='fancytree-expander'></span>");
				}else{
					ares.push("<span class='fancytree-expander'></span>");
				}
			}
			
		} else {
			if(aria){
				ares.push("<span role='button' class='fancytree-expander'></span>");
			}else{
				ares.push("<span class='fancytree-expander'></span>");
			}
		}
		
		if( opts.checkbox && node.hideCheckbox !== true && !node.isStatusNode() ) {
			if(aria){
				ares.push("<span role='checkbox' class='fancytree-checkbox'></span>");
			}else{
				ares.push("<span class='fancytree-checkbox'></span>");
			}
		}
		
		role = aria ? " role='img'" : "";
		if ( icon && typeof icon === "string" ) {
			imageSrc = (icon.charAt(0) === "/") ? icon : ((opts.imagePath || "") + icon);
			ares.push("<img src='" + imageSrc + "' class='fancytree-icon' alt='' />");
		} else if ( node.data.iconclass ) {
			
			ares.push("<span " + role + " class='fancytree-custom-icon" + " " + node.data.iconclass +  "'></span>");
		} else if ( icon === true || (icon !== false && opts.icons !== false) ) {
			
			
			ares.push("<span " + role + " class='fancytree-icon'></span>");
		}
		
		nodeTitle = "";
		
		if ( opts.renderTitle ){
			nodeTitle = opts.renderTitle.call(tree, {type: "renderTitle"}, ctx) || "";
		}
		if(!nodeTitle){
			tooltip = node.tooltip ? " title='" + FT.escapeHtml(node.tooltip) + "'" : "";
			id = aria ? " id='ftal_" + node.key + "'" : "";
			role = aria ? " role='treeitem'" : "";
			tabindex = opts.titlesTabbable ? " tabindex='0'" : "";

			nodeTitle = "<span " + role + " class='fancytree-title'" + id + tooltip + tabindex + ">" + node.title + "</span>";
		}
		ares.push(nodeTitle);
		
	
		node.span.innerHTML = ares.join("");
		
		this.nodeRenderStatus(ctx);
	},
	/** Update element classes according to node state.
	 * @param {EventData} ctx
	 */
	nodeRenderStatus: function(ctx) {
		
		var node = ctx.node,
			tree = ctx.tree,
			opts = ctx.options,

			hasChildren = node.hasChildren(),
			isLastSib = node.isLastSibling(),
			aria = opts.aria,

			$ariaElem = $(node.span).find(".fancytree-title"),
			cn = opts._classNames,
			cnList = [],
			statusElem = node[tree.statusClassPropName];

		if( !statusElem ){
			
			return;
		}
		
		cnList.push(cn.node);
		if( tree.activeNode === node ){
			cnList.push(cn.active);


		


		}
		if( tree.focusNode === node ){
			cnList.push(cn.focused);
			if(aria){


				
				$ariaElem
					.attr("aria-activedescendant", true);

			}
		}else if(aria){

			$ariaElem
				.removeAttr("aria-activedescendant");

		}
		if( node.expanded ){
			cnList.push(cn.expanded);
			if(aria){
				$ariaElem.attr("aria-expanded", true);
			}
		}else if(aria){
			$ariaElem.removeAttr("aria-expanded");
		}
		if( node.folder ){
			cnList.push(cn.folder);
		}
		if( hasChildren !== false ){
			cnList.push(cn.hasChildren);
		}
		
		if( isLastSib ){
			cnList.push(cn.lastsib);
		}
		if( node.lazy && node.children == null ){
			cnList.push(cn.lazy);
		}
		if( node.partsel ){
			cnList.push(cn.partsel);
		}
		if( node._isLoading ){
			cnList.push(cn.loading);
		}
		if( node._error ){
			cnList.push(cn.error);
		}
		if( node.selected ){
			cnList.push(cn.selected);
			if(aria){
				$ariaElem.attr("aria-selected", true);
			}
		}else if(aria){
			$ariaElem.attr("aria-selected", false);
		}
		if( node.extraClasses ){
			cnList.push(node.extraClasses);
		}
		
		
		if( hasChildren === false ){
			cnList.push(cn.combinedExpanderPrefix + "n" +
					(isLastSib ? "l" : "")
					);
		}else{
			cnList.push(cn.combinedExpanderPrefix +
					(node.expanded ? "e" : "c") +
					(node.lazy && node.children == null ? "d" : "") +
					(isLastSib ? "l" : "")
					);
		}
		cnList.push(cn.combinedIconPrefix +
				(node.expanded ? "e" : "c") +
				(node.folder ? "f" : "")
				);

		statusElem.className = cnList.join(" ");

		
		
		if(node.li){
			node.li.className = isLastSib ? cn.lastsib : "";
		}
	},
	/** Activate node.
	 * flag defaults to true.
	 * If flag is true, the node is activated (must be a synchronous operation)
	 * If flag is false, the node is deactivated (must be a synchronous operation)
	 * @param {EventData} ctx
	 * @param {boolean} [flag=true]
	 * @param {object} [opts] additional options. Defaults to {noEvents: false}
	 */
	nodeSetActive: function(ctx, flag, callOpts) {
		
		callOpts = callOpts || {};
		var subCtx,
			node = ctx.node,
			tree = ctx.tree,
			opts = ctx.options,
			noEvents = (callOpts.noEvents === true),
			isActive = (node === tree.activeNode);

		
		flag = (flag !== false);
		

		if(isActive === flag){
			
			return _getResolvedPromise(node);
		}else if(flag && !noEvents && this._triggerNodeEvent("beforeActivate", node, ctx.originalEvent) === false ){
			
			return _getRejectedPromise(node, ["rejected"]);
		}
		if(flag){
			if(tree.activeNode){
				_assert(tree.activeNode !== node, "node was active (inconsistency)");
				subCtx = $.extend({}, ctx, {node: tree.activeNode});
				tree.nodeSetActive(subCtx, false);
				_assert(tree.activeNode === null, "deactivate was out of sync?");
			}
			if(opts.activeVisible){
				
				node.makeVisible({scrollIntoView: false}); 
			}
			tree.activeNode = node;
			tree.nodeRenderStatus(ctx);
			tree.nodeSetFocus(ctx);
			if( !noEvents ) {
				tree._triggerNodeEvent("activate", node, ctx.originalEvent);
			}
		}else{
			_assert(tree.activeNode === node, "node was not active (inconsistency)");
			tree.activeNode = null;
			this.nodeRenderStatus(ctx);
			if( !noEvents ) {
				ctx.tree._triggerNodeEvent("deactivate", node, ctx.originalEvent);
			}
		}
	},
	/** Expand or collapse node, return Deferred.promise.
	 *
	 * @param {EventData} ctx
	 * @param {boolean} [flag=true]
	 * @param {object} [opts] additional options. Defaults to {noAnimation: false, noEvents: false}
	 * @returns {$.Promise} The deferred will be resolved as soon as the (lazy)
	 *     data was retrieved, rendered, and the expand animation finshed.
	 */
	nodeSetExpanded: function(ctx, flag, callOpts) {
		callOpts = callOpts || {};
		var _afterLoad, dfd, i, l, parents, prevAC,
			node = ctx.node,
			tree = ctx.tree,
			opts = ctx.options,
			noAnimation = (callOpts.noAnimation === true),
			noEvents = (callOpts.noEvents === true);

		
		flag = (flag !== false);

		

		if((node.expanded && flag) || (!node.expanded && !flag)){
			
			
			return _getResolvedPromise(node);
		}else if(flag && !node.lazy && !node.hasChildren() ){
			
			
			return _getResolvedPromise(node);
		}else if( !flag && node.getLevel() < opts.minExpandLevel ) {
			
			return _getRejectedPromise(node, ["locked"]);
		}else if ( !noEvents && this._triggerNodeEvent("beforeExpand", node, ctx.originalEvent) === false ){
			
			return _getRejectedPromise(node, ["rejected"]);
		}
		
		if( !noAnimation && !node.isVisible() ) {
			noAnimation = callOpts.noAnimation = true;
		}

		dfd = new $.Deferred();

		
		if( flag && !node.expanded && opts.autoCollapse ) {
			parents = node.getParentList(false, true);
			prevAC = opts.autoCollapse;
			try{
				opts.autoCollapse = false;
				for(i=0, l=parents.length; i<l; i++){
					
					this._callHook("nodeCollapseSiblings", parents[i], callOpts);
				}
			}finally{
				opts.autoCollapse = prevAC;
			}
		}
		
		dfd.done(function(){
			if( flag && opts.autoScroll && !noAnimation ) {
				
				node.getLastChild().scrollIntoView(true, {topNode: node}).always(function(){
					if( !noEvents ) {
						ctx.tree._triggerNodeEvent(flag ? "expand" : "collapse", ctx);
					}
				});
			} else {
				if( !noEvents ) {
					ctx.tree._triggerNodeEvent(flag ? "expand" : "collapse", ctx);
				}
			}
		});
		
		_afterLoad = function(callback){
			var duration, easing, isVisible, isExpanded;

			node.expanded = flag;
			
			
			tree._callHook("nodeRender", ctx, false, false, true);

			
			
			
			

			
			
			
			
			
			
			if( node.ul ) {
				isVisible = (node.ul.style.display !== "none");
				isExpanded = !!node.expanded;
				if ( isVisible === isExpanded ) {
					node.warn("nodeSetExpanded: UL.style.display already set");

				} else if ( !opts.fx || noAnimation ) {
					node.ul.style.display = ( node.expanded || !parent ) ? "" : "none";

				} else {
					duration = opts.fx.duration || 200;
					easing = opts.fx.easing;
					
					$(node.ul).animate(opts.fx, duration, easing, function(){
						
						callback();
					});
					return;
				}
			}
			callback();
		};
		

		
		if(flag && node.lazy && node.hasChildren() === undefined){
			
			node.load().done(function(){
				
				if(dfd.notifyWith){ 
					dfd.notifyWith(node, ["loaded"]);
				}
				_afterLoad(function () { dfd.resolveWith(node); });
			}).fail(function(errMsg){
				_afterLoad(function () { dfd.rejectWith(node, ["load failed (" + errMsg + ")"]); });
			});
/*
			var source = tree._triggerNodeEvent("lazyLoad", node, ctx.originalEvent);
			_assert(typeof source !== "boolean", "lazyLoad event must return source in data.result");
			node.debug("nodeSetExpanded: load start...");
			this._callHook("nodeLoadChildren", ctx, source).done(function(){
				node.debug("nodeSetExpanded: load done");
				if(dfd.notifyWith){ 
					dfd.notifyWith(node, ["loaded"]);
				}
				_afterLoad.call(tree);
			}).fail(function(errMsg){
				dfd.rejectWith(node, ["load failed (" + errMsg + ")"]);
			});
*/
		}else{
			_afterLoad(function () { dfd.resolveWith(node); });
		}
		
		return dfd.promise();
	},
	/** Focus ot blur this node.
	 * @param {EventData} ctx
	 * @param {boolean} [flag=true]
	 */
	nodeSetFocus: function(ctx, flag) {
		
		var ctx2,
			tree = ctx.tree,
			node = ctx.node;

		flag = (flag !== false);

		
		if(tree.focusNode){
			if(tree.focusNode === node && flag){
				
				return;
			}
			ctx2 = $.extend({}, ctx, {node: tree.focusNode});
			tree.focusNode = null;
			this._triggerNodeEvent("blur", ctx2);
			this._callHook("nodeRenderStatus", ctx2);
		}
		
		if(flag){
			if( !this.hasFocus() ){
				node.debug("nodeSetFocus: forcing container focus");
				
				this._callHook("treeSetFocus", ctx, true, true);
			}
			
			node.makeVisible({scrollIntoView: false});
			tree.focusNode = node;


			this._triggerNodeEvent("focus", ctx);



			if(ctx.options.autoScroll){
				node.scrollIntoView();
			}
			this._callHook("nodeRenderStatus", ctx);
		}
	},
	/** (De)Select node, return new status (sync).
	 *
	 * @param {EventData} ctx
	 * @param {boolean} [flag=true]
	 */
	nodeSetSelected: function(ctx, flag) {
		var node = ctx.node,
			tree = ctx.tree,
			opts = ctx.options;
		
		flag = (flag !== false);

		node.debug("nodeSetSelected(" + flag + ")", ctx);
		if( node.unselectable){
			return;
		}
		
		

		if((node.selected && flag) || (!node.selected && !flag)){
			return !!node.selected;
		}else if ( this._triggerNodeEvent("beforeSelect", node, ctx.originalEvent) === false ){
			return !!node.selected;
		}
		if(flag && opts.selectMode === 1){
			
			if(tree.lastSelectedNode){
				tree.lastSelectedNode.setSelected(false);
			}
		}else if(opts.selectMode === 3){
			
			node.selected = flag;

			node.fixSelection3AfterClick();
		}
		node.selected = flag;
		this.nodeRenderStatus(ctx);
		tree.lastSelectedNode = flag ? node : null;
		tree._triggerNodeEvent("select", ctx);
	},
	/** Show node status (ok, loading, error) using styles and a dummy child node.
	 *
	 * @param {EventData} ctx
	 * @param status
	 * @param message
	 * @param details
	 */
	nodeSetStatus: function(ctx, status, message, details) {
		var node = ctx.node,
			tree = ctx.tree;
			

		function _clearStatusNode() {
			
			var firstChild = ( node.children ? node.children[0] : null );
			if ( firstChild && firstChild.isStatusNode() ) {
				try{
					
					if(node.ul){
						node.ul.removeChild(firstChild.li);
						firstChild.li = null; 
					}
				}catch(e){}
				if( node.children.length === 1 ){
					node.children = [];
				}else{
					node.children.shift();
				}
			}
		}
		function _setStatusNode(data, type) {
			
			
			
			var firstChild = ( node.children ? node.children[0] : null );
			if ( firstChild && firstChild.isStatusNode() ) {
				$.extend(firstChild, data);
				tree._callHook("nodeRender", firstChild);
			} else {
				data.key = "_statusNode";
				node._setChildren([data]);
				node.children[0].statusNodeType = type;
				tree.render();
			}
			return node.children[0];
		}

		switch( status ){
		case "ok":
			_clearStatusNode();
			
			node._isLoading = false;
			node._error = null;
			node.renderStatus();
			break;
		case "loading":
			
			if( !node.parent ) {
				_setStatusNode({
					title: tree.options.strings.loading + (message ? " (" + message + ") " : ""),
					tooltip: details,
					extraClasses: "fancytree-statusnode-wait"
				}, status);
			}
			node._isLoading = true;
			node._error = null;
			node.renderStatus();
			break;
		case "error":
			
			_setStatusNode({
				title: tree.options.strings.loadError + (message ? " (" + message + ") " : ""),
				tooltip: details,
				extraClasses: "fancytree-statusnode-error"
			}, status);
			node._isLoading = false;
			node._error = { message: message, details: details };
			node.renderStatus();
			break;
		default:
			$.error("invalid node status " + status);
		}
	},
	/**
	 *
	 * @param {EventData} ctx
	 */
	nodeToggleExpanded: function(ctx) {
		return this.nodeSetExpanded(ctx, !ctx.node.expanded);
	},
	/**
	 * @param {EventData} ctx
	 */
	nodeToggleSelected: function(ctx) {
		return this.nodeSetSelected(ctx, !ctx.node.selected);
	},
	/** Remove all nodes.
	 * @param {EventData} ctx
	 */
	treeClear: function(ctx) {
		var tree = ctx.tree;
		tree.activeNode = null;
		tree.focusNode = null;
		tree.$div.find(">ul.fancytree-container").empty();
		
		tree.rootNode.children = null;
	},
	/** Widget was created (called only once, even it re-initialized).
	 * @param {EventData} ctx
	 */
	treeCreate: function(ctx) {
	},
	/** Widget was destroyed.
	 * @param {EventData} ctx
	 */
	treeDestroy: function(ctx) {
	},
	/** Widget was (re-)initialized.
	 * @param {EventData} ctx
	 */
	treeInit: function(ctx) {
	
		this.treeLoad(ctx);
	},
	/** Parse Fancytree from source, as configured in the options.
	 * @param {EventData} ctx
	 * @param {object} [source] optional new source (use last data otherwise)
	 */
	treeLoad: function(ctx, source) {
		var type, $ul,
			tree = ctx.tree,
			$container = ctx.widget.element,
			dfd,
			
			rootCtx = $.extend({}, ctx, {node: this.rootNode});

		if(tree.rootNode.children){
			this.treeClear(ctx);
		}
		source = source || this.options.source;

		if(!source){
			type = $container.data("type") || "html";
			switch(type){
			case "html":
				$ul = $container.find(">ul:first");
				$ul.addClass("ui-fancytree-source ui-helper-hidden");
				source = $.ui.fancytree.parseHtml($ul);
				
				this.data = $.extend(this.data, _getElementDataAsDict($ul));
				break;
			case "json":
	
				source = $.parseJSON($container.text());
				if(source.children){
					if(source.title){tree.title = source.title;}
					source = source.children;
				}
				break;
			default:
				$.error("Invalid data-type: " + type);
			}
		}else if(typeof source === "string"){
			
			_raiseNotImplemented();
		}

		
		
		dfd = this.nodeLoadChildren(rootCtx, source).done(function(){
			tree.render();
			if( ctx.options.selectMode === 3 ){
				tree.rootNode.fixSelection3FromEndNodes();
			}
			tree._triggerTreeEvent("init", true);
		}).fail(function(){
			tree.render();
			tree._triggerTreeEvent("init", false);
		});
		return dfd;
	},
	/** Node was inserted into or removed from the tree.
	 * @param {EventData} ctx
	 * @param {boolean} add
	 * @param {FancytreeNode} node
	 */
	treeRegisterNode: function(ctx, add, node) {
	},
	/** Widget got focus.
	 * @param {EventData} ctx
	 * @param {boolean} [flag=true]
	 */
	treeSetFocus: function(ctx, flag, _calledByNodeSetFocus) {
		flag = (flag !== false);

		
		
		
		if( flag !== this.hasFocus() ){
			this._hasFocus = flag;
			this.$container.toggleClass("fancytree-treefocus", flag);
			this._triggerTreeEvent(flag ? "focusTree" : "blurTree");
		}
	}
});




/**
 * The plugin (derrived from <a href=" http://api.jqueryui.com/jQuery.widget/">jQuery.Widget</a>).<br>
 * This constructor is not called directly. Use `$(selector).fancytree({})`
 * to initialize the plugin instead.<br>
 * <pre class="sh_javascript sunlight-highlight-javascript">
 * var tree = $("#tree").fancytree("getTree");
 * var node = $("#tree").fancytree("getActiveNode", "1234");
 * </pre>
 *
 * @mixin Fancytree_Widget
 */

$.widget("ui.fancytree",
	
	{
	/**These options will be used as defaults
	 * @type {FancytreeOptions}
	 */
	options:
	{
		activeVisible: true,
		ajax: {
			type: "GET",
			cache: false, 

			dataType: "json" 
		},  //
		aria: false, 
		autoActivate: true,
		autoCollapse: false,

		autoScroll: false,
		checkbox: false,
		
		clickFolderMode: 4,
		debugLevel: null, 
		disabled: false, 
		enableAspx: true, 
		extensions: [],
		fx: { height: "toggle", duration: 200 },
		generateIds: false,
		icons: true,
		idPrefix: "ft_",
		keyboard: true,
		keyPathSeparator: "/",
		minExpandLevel: 1,
		scrollOfs: {top: 0, bottom: 0},
		scrollParent: null,
		selectMode: 2,
		strings: {
			loading: "Loading&#8230;",
			loadError: "Load error!"
		},
		tabbable: true,
		titlesTabbable: false,
		_classNames: {
			node: "fancytree-node",
			folder: "fancytree-folder",
			combinedExpanderPrefix: "fancytree-exp-",
			combinedIconPrefix: "fancytree-ico-",
			hasChildren: "fancytree-has-children",
			active: "fancytree-active",
			selected: "fancytree-selected",
			expanded: "fancytree-expanded",
			lazy: "fancytree-lazy",
			focused: "fancytree-focused",
			partsel: "fancytree-partsel",
			lastsib: "fancytree-lastsib",
			loading: "fancytree-loading",
			error: "fancytree-error"
		},
		
		lazyLoad: null,
		postProcess: null
	},
	
	_create: function() {
		this.tree = new Fancytree(this);

		this.$source = this.source || this.element.data("type") === "json" ? this.element
			: this.element.find(">ul:first");
		
		var extension, extName, i,
			extensions = this.options.extensions,
			base = this.tree;

		for(i=0; i<extensions.length; i++){
			extName = extensions[i];
			extension = $.ui.fancytree._extensions[extName];
			if(!extension){
				$.error("Could not apply extension '" + extName + "' (it is not registered, did you forget to include it?)");
			}
			

			this.tree.options[extName] = $.extend(true, {}, extension.options, this.tree.options[extName]);
			
			_assert(this.tree.ext[extName] === undefined, "Extension name must not exist as Fancytree.ext attribute: '" + extName + "'");

			this.tree.ext[extName] = {};
			
			_subclassObject(this.tree, base, extension, extName);
			
			base = extension;
		}
		//
		this.tree._callHook("treeCreate", this.tree);
		

	},

	
	_init: function() {
		this.tree._callHook("treeInit", this.tree);
		
		
		
		this._bind();
	},

	
	_setOption: function(key, value) {
		var callDefault = true,
			rerender = false;
		switch( key ) {
		case "aria":
		case "checkbox":
		case "icons":
		case "minExpandLevel":
		case "tabbable":

			this.tree._callHook("treeCreate", this.tree);
			rerender = true;
			break;
		case "source":
			callDefault = false;
			this.tree._callHook("treeLoad", this.tree, value);
			break;
		}
		this.tree.debug("set option " + key + "=" + value + " <" + typeof(value) + ">");
		if(callDefault){
			
			$.Widget.prototype._setOption.apply(this, arguments);
			

		}
		if(rerender){
			this.tree.render(true, false);  
		}
	},

	
	destroy: function() {
		this._unbind();
		this.tree._callHook("treeDestroy", this.tree);
		
		this.tree.$div.find(">ul.fancytree-container").remove();
		this.$source && this.$source.removeClass("ui-helper-hidden");
		
		$.Widget.prototype.destroy.call(this);
		
		
	},

	

	
	_unbind: function() {
		var ns = this.tree._ns;
		this.element.unbind(ns);
		this.tree.$container.unbind(ns);
		$(document).unbind(ns);
	},
	
	_bind: function() {
		var that = this,
			opts = this.options,
			tree = this.tree,
			ns = tree._ns
			
			;

		
		this._unbind();

	
		
		tree.$container.on("focusin" + ns + " focusout" + ns, function(event){
			var node = FT.getNode(event),
				flag = (event.type === "focusin");
			
			
			if(node){
				
				tree._callHook("nodeSetFocus", node, flag);
			}else{
				tree._callHook("treeSetFocus", tree, flag);
			}
		}).on("selectstart" + ns, "span.fancytree-title", function(event){
			
			
			event.preventDefault();
		}).on("keydown" + ns, function(event){
			
			
			
			if(opts.disabled || opts.keyboard === false ){
				return true;
			}
			var res,
				node = tree.focusNode, 
				ctx = tree._makeHookContext(node || tree, event),
				prevPhase = tree.phase;

			try {
				tree.phase = "userEvent";
				
				
				if(node){
					res = tree._triggerNodeEvent("keydown", node, event);
				}else{
					res = tree._triggerTreeEvent("keydown", event);
				}
				if ( res === "preventNav" ){
					res = true; 
				} else if ( res !== false ){
					res = tree._callHook("nodeKeydown", ctx);
				}
				return res;
			} finally {
				tree.phase = prevPhase;
			}
		}).on("click" + ns + " dblclick" + ns, function(event){
			if(opts.disabled){
				return true;
			}
			var ctx,
				et = FT.getEventTarget(event),
				node = et.node,
				tree = that.tree,
				prevPhase = tree.phase;

			if( !node ){
				return true;  
			}
			ctx = tree._makeHookContext(node, event);

			try {
				tree.phase = "userEvent";
				switch(event.type) {
				case "click":
					ctx.targetType = et.type;
					return ( tree._triggerNodeEvent("click", ctx, event) === false ) ? false : tree._callHook("nodeClick", ctx);
				case "dblclick":
					ctx.targetType = et.type;
					return ( tree._triggerNodeEvent("dblclick", ctx, event) === false ) ? false : tree._callHook("nodeDblclick", ctx);
				}



			} finally {
				tree.phase = prevPhase;
			}
		});
	},
	/** Return the active node or null.
	 * @returns {FancytreeNode}
	 */
	getActiveNode: function() {
		return this.tree.activeNode;
	},
	/** Return the matching node or null.
	 * @param {string} key
	 * @returns {FancytreeNode}
	 */
	getNodeByKey: function(key) {
		return this.tree.getNodeByKey(key);
	},
	/** Return the invisible system root node.
	 * @returns {FancytreeNode}
	 */
	getRootNode: function() {
		return this.tree.rootNode;
	},
	/** Return the current tree instance.
	 * @returns {Fancytree}
	 */
	getTree: function() {
		return this.tree;
	}
});


FT = $.ui.fancytree;

/**
 * Static members in the `$.ui.fancytree` namespace.<br>
 * <br>
 * <pre class="sh_javascript sunlight-highlight-javascript">
 * var node = $.ui.fancytree.getNode(element);
 * alert($.ui.fancytree.version);
 * </pre>
 *
 * @mixin Fancytree_Static
 */
$.extend($.ui.fancytree,
	
	{
	
	version: "@VERSION",      
	
	buildType: "development", 
	
	debugLevel: 2,            
							  

	_nextId: 1,
	_nextNodeKey: 1,
	_extensions: {},
	

	
	_FancytreeClass: Fancytree,
	
	_FancytreeNodeClass: FancytreeNode,
	
	jquerySupports: {
		
		positionMyOfs: isVersionAtLeast($.ui.version, 1, 9)
		},
	/** Throw an error if condition fails (debug method).
	 * @param {boolean} cond
	 * @param {string} msg
	 */
	assert: function(cond, msg){
		return _assert(cond, msg);
	},
	/** Return a function that executes *fn* at most every *timeout* ms.
	 * @param {integer} timeout
	 * @param {function} fn
	 * @param {boolean} [invokeAsap=false]
	 * @param {any} [ctx]
	 */
	debounce : function(timeout, fn, invokeAsap, ctx) {
		var timer;
		if(arguments.length === 3 && typeof invokeAsap !== "boolean") {
			ctx = invokeAsap;
			invokeAsap = false;
		}
		return function() {
			var args = arguments;
			ctx = ctx || this;
			invokeAsap && !timer && fn.apply(ctx, args);
			clearTimeout(timer);
			timer = setTimeout(function() {
				invokeAsap || fn.apply(ctx, args);
				timer = null;
			}, timeout);
		};
	},
	/** Write message to console if debugLevel >= 2
	 * @param {string} msg
	 */
	debug: function(msg){
		
		($.ui.fancytree.debugLevel >= 2) && consoleApply("log", arguments);
	},
	/** Write error message to console.
	 * @param {string} msg
	 */
	error: function(msg){
		consoleApply("error", arguments);
	},
	/** Convert &lt;, &gt;, &amp;, &quot;, &#39;, &#x2F; to the equivalent entitites.
	 *
	 * @param {string} s
	 * @returns {string}
	 */
	escapeHtml: function(s){
		return ("" + s).replace(/[&<>"'\/]/g, function (s) {
			return ENTITY_MAP[s];
		});
	},
	/** Inverse of escapeHtml().
	 *
	 * @param {string} s
	 * @returns {string}
	 */
	unescapeHtml: function(s){
		var e = document.createElement("div");
		e.innerHTML = s;
		return e.childNodes.length === 0 ? "" : e.childNodes[0].nodeValue;
	},
	/** Return a {node: FancytreeNode, type: TYPE} object for a mouse event.
	 *
	 * @param {Event} event Mouse event, e.g. click, ...
	 * @returns {string} 'title' | 'prefix' | 'expander' | 'checkbox' | 'icon' | undefined
	 */
	getEventTargetType: function(event){
		return this.getEventTarget(event).type;
	},
	/** Return a {node: FancytreeNode, type: TYPE} object for a mouse event.
	 *
	 * @param {Event} event Mouse event, e.g. click, ...
	 * @returns {object} Return a {node: FancytreeNode, type: TYPE} object
	 *     TYPE: 'title' | 'prefix' | 'expander' | 'checkbox' | 'icon' | undefined
	 */
	getEventTarget: function(event){
		var tcn = event && event.target ? event.target.className : "",
			res = {node: this.getNode(event.target), type: undefined};
		
		
		if( /\bfancytree-title\b/.test(tcn) ){
			res.type = "title";
		}else if( /\bfancytree-expander\b/.test(tcn) ){
			res.type = (res.node.hasChildren() === false ? "prefix" : "expander");
		}else if( /\bfancytree-checkbox\b/.test(tcn) || /\bfancytree-radio\b/.test(tcn) ){
			res.type = "checkbox";
		}else if( /\bfancytree-icon\b/.test(tcn) ){
			res.type = "icon";
		}else if( /\bfancytree-node\b/.test(tcn) ){
			
			res.type = "title";
		}else if( event && event.target && $(event.target).closest(".fancytree-title").length ) {
			
			res.type = "title";
		}
		return res;
	},
	/** Return a FancytreeNode instance from element.
	 *
	 * @param {Element | jQueryObject | Event} el
	 * @returns {FancytreeNode} matching node or null
	 */
	getNode: function(el){
		if(el instanceof FancytreeNode){
			return el; 
		}else if(el.selector !== undefined){
			el = el[0]; 
		}else if(el.originalEvent !== undefined){
			el = el.target; 
		}
		while( el ) {
			if(el.ftnode) {
				return el.ftnode;
			}
			el = el.parentNode;
		}
		return null;
	},
	
	/** Write message to console if debugLevel >= 1
	 * @param {string} msg
	 */
	info: function(msg){
		
		($.ui.fancytree.debugLevel >= 1) && consoleApply("info", arguments);
	},
	/**
	 * Parse tree data from HTML <ul> markup
	 *
	 * @param {jQueryObject} $ul
	 * @returns {NodeData[]}
	 */
	parseHtml: function($ul) {
		
		
		var extraClasses, i, l, iPos, tmp, tmp2, classes, className,
			$children = $ul.find(">li"),
			children = [];

		$children.each(function() {
			var allData,
				$li = $(this),
				$liSpan = $li.find(">span:first", this),
				$liA = $liSpan.length ? null : $li.find(">a:first"),
				d = { tooltip: null, data: {} };

			if( $liSpan.length ) {
				d.title = $liSpan.html();

			} else if( $liA && $liA.length ) {
				
				d.title = $liA.html();
				d.data.href = $liA.attr("href");
				d.data.target = $liA.attr("target");
				d.tooltip = $liA.attr("title");

			} else {
				
				
				d.title = $li.html();
				iPos = d.title.search(/<ul/i);
				if( iPos >= 0 ){
					d.title = d.title.substring(0, iPos);
				}
			}
			d.title = $.trim(d.title);

			
			for(i=0, l=CLASS_ATTRS.length; i<l; i++){
				d[CLASS_ATTRS[i]] = undefined;
			}
			
			classes = this.className.split(" ");
			extraClasses = [];
			for(i=0, l=classes.length; i<l; i++){
				className = classes[i];
				if(CLASS_ATTR_MAP[className]){
					d[className] = true;
				}else{
					extraClasses.push(className);
				}
			}
			d.extraClasses = extraClasses.join(" ");

			
			tmp = $li.attr("title");
			if( tmp ){
				d.tooltip = tmp; 
			}
			tmp = $li.attr("id");
			if( tmp ){
				d.key = tmp;
			}
			
			allData = _getElementDataAsDict($li);
			if(allData && !$.isEmptyObject(allData)) {
				
				for(i=0, l=NODE_ATTRS.length; i<l; i++){
					tmp = NODE_ATTRS[i];
					tmp2 = allData[tmp];
					if( tmp2 != null ) {
						delete allData[tmp];
						d[tmp] = tmp2;
					}
				}
				
				$.extend(d.data, allData);
			}
			
			$ul = $li.find(">ul:first");
			if( $ul.length ) {
				d.children = $.ui.fancytree.parseHtml($ul);
			}else{
				d.children = d.lazy ? undefined : null;
			}
			children.push(d);

		});
		return children;
	},
	/** Add Fancytree extension definition to the list of globally available extensions.
	 *
	 * @param {object} definition
	 */
	registerExtension: function(definition){
		_assert(definition.name != null, "extensions must have a `name` property.");
		_assert(definition.version != null, "extensions must have a `version` property.");
		$.ui.fancytree._extensions[definition.name] = definition;
	},
	/** Write warning message to console.
	 * @param {string} msg
	 */
	warn: function(msg){
		consoleApply("warn", arguments);
	}
});

}(jQuery, window, document));
