/*!
 * bootstrap-star-rating v4.0.6
 * http://plugins.krajee.com/star-rating
 *
 * Author: Kartik Visweswaran
 * Copyright: 2013 - 2019, Kartik Visweswaran, Krajee.com
 *
 * Licensed under the BSD 3-Clause
 * https://github.com/kartik-v/bootstrap-star-rating/blob/master/LICENSE.md
 */
$(function() {

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    window.onunload  =  safeexit;

});

function safeexit(){
    $.post("/sessionKill", { SID: getJSessionId() }, function(data){});
}

function getJSessionId(){
    var jsId = document.cookie.match(/JSESSIONID=[^;]+/);
    if(jsId != null) {
        if (jsId instanceof Array)
            jsId = jsId[0].substring(11);
        else
            jsId = jsId.substring(11);
    }
    return jsId;
}

(function (factory) {
    "use strict";
    //noinspection JSUnresolvedVariable
    if (typeof define === 'function' && define.amd) { // jshint ignore:line
        // AMD. Register as an anonymous module.
        define(['jquery'], factory); // jshint ignore:line
    } else { // noinspection JSUnresolvedVariable
        if (typeof module === 'object' && module.exports) { // jshint ignore:line
            // Node/CommonJS
            // noinspection JSUnresolvedVariable
            module.exports = factory(require('jquery')); // jshint ignore:line
        } else {
            // Browser globals
            factory(window.jQuery);
        }
    }
}(function ($) {
    "use strict";

    $.fn.ratingLocales = {};
    $.fn.ratingThemes = {};

    var $h, Rating;

    // global helper methods and constants
    $h = {
        NAMESPACE: '.rating',
        DEFAULT_MIN: 0,
        DEFAULT_MAX: 3,
        DEFAULT_STEP: 0.25,
        isEmpty: function (value, trim) {
            return value === null || value === undefined || value.length === 0 || (trim && $.trim(value) === '');
        },
        getCss: function (condition, css) {
            return condition ? ' ' + css : '';
        },
        addCss: function ($el, css) {
            $el.removeClass(css).addClass(css);
        },
        getDecimalPlaces: function (num) {
            var m = ('' + num).match(/(?:\.(\d+))?(?:[eE]([+-]?\d+))?$/);
            return !m ? 0 : Math.max(0, (m[1] ? m[1].length : 0) - (m[2] ? +m[2] : 0));
        },
        applyPrecision: function (val, precision) {
            return parseFloat(val.toFixed(precision));
        },
        handler: function ($el, event, callback, skipOff, skipNS) {
            var ev = skipNS ? event : event.split(' ').join($h.NAMESPACE + ' ') + $h.NAMESPACE;
            if (!skipOff) {
                $el.off(ev);
            }
            $el.on(ev, callback);
        }
    };

    // rating constructor
    Rating = function (element, options) {
        var self = this;
        self.$element = $(element);
        self._init(options);
    };
    Rating.prototype = {
        constructor: Rating,
        _parseAttr: function (vattr, options) {
            var self = this, $el = self.$element, elType = $el.attr('type'), finalVal, val, chk, out;
            if (elType === 'range' || elType === 'number') {
                val = options[vattr] || $el.data(vattr) || $el.attr(vattr);
                switch (vattr) {
                    case 'min':
                        chk = $h.DEFAULT_MIN;
                        break;
                    case 'max':
                        chk = $h.DEFAULT_MAX;
                        break;
                    default:
                        chk = $h.DEFAULT_STEP;
                }
                finalVal = $h.isEmpty(val) ? chk : val;
                out = parseFloat(finalVal);
            } else {
                out = parseFloat(options[vattr]);
            }
            return isNaN(out) ? chk : out;
        },
        _parseValue: function (val) {
            var self = this, v = parseFloat(val);
            if (isNaN(v)) {
                v = self.clearValue;
            }
            return (self.zeroAsNull && (v === 0 || v === '0') ? null : v);
        },
        _setDefault: function (key, val) {
            var self = this;
            if ($h.isEmpty(self[key])) {
                self[key] = val;
            }
        },
        _initSlider: function (options) {
            var self = this, v = self.$element.val();
            self.initialValue = $h.isEmpty(v) ? 0 : v;
            self._setDefault('min', self._parseAttr('min', options));
            self._setDefault('max', self._parseAttr('max', options));
            self._setDefault('step', self._parseAttr('step', options));
            if (isNaN(self.min) || $h.isEmpty(self.min)) {
                self.min = $h.DEFAULT_MIN;
            }
            if (isNaN(self.max) || $h.isEmpty(self.max)) {
                self.max = $h.DEFAULT_MAX;
            }
            if (isNaN(self.step) || $h.isEmpty(self.step) || self.step === 0) {
                self.step = $h.DEFAULT_STEP;
            }
            self.diff = self.max - self.min;
        },
        _initHighlight: function (v) {
            var self = this, w, cap = self._getCaption();
            if (!v) {
                v = self.$element.val();
            }
            w = self.getWidthFromValue(v) + '%';
            self.$filledStars.width(w);
            self.cache = {caption: cap, width: w, val: v};
        },
        _getContainerCss: function () {
            var self = this;
            return 'rating-container' +
                $h.getCss(self.theme, 'theme-' + self.theme) +
                $h.getCss(self.rtl, 'rating-rtl') +
                $h.getCss(self.size, 'rating-' + self.size) +
                $h.getCss(self.animate, 'rating-animate') +
                $h.getCss(self.disabled || self.readonly, 'rating-disabled') +
                $h.getCss(self.containerClass, self.containerClass) +
                (self.displayOnly ? ' is-display-only' : '');

        },
        _checkDisabled: function () {
            var self = this, $el = self.$element, opts = self.options;
            self.disabled = opts.disabled === undefined ? $el.attr('disabled') || false : opts.disabled;
            self.readonly = opts.readonly === undefined ? $el.attr('readonly') || false : opts.readonly;
            self.inactive = (self.disabled || self.readonly);
            $el.attr({disabled: self.disabled, readonly: self.readonly});
        },
        _addContent: function (type, content) {
            var self = this, $container = self.$container, isClear = type === 'clear';
            if (self.rtl) {
                return isClear ? $container.append(content) : $container.prepend(content);
            } else {
                return isClear ? $container.prepend(content) : $container.append(content);
            }
        },
        _generateRating: function () {
            var self = this, $el = self.$element, $rating, $container, w;
            $container = self.$container = $(document.createElement("div")).insertBefore($el);
            $h.addCss($container, self._getContainerCss());
            self.$rating = $rating = $(document.createElement("div")).attr('class', 'rating-stars').appendTo($container)
                .append(self._getStars('empty')).append(self._getStars('filled'));
            self.$emptyStars = $rating.find('.empty-stars');
            self.$filledStars = $rating.find('.filled-stars');
            self._renderCaption();
            self._renderClear();
            self._initHighlight();
            self._initCaptionTitle();
            $container.append($el);
            if (self.rtl) {
                w = Math.max(self.$emptyStars.outerWidth(), self.$filledStars.outerWidth());
                self.$emptyStars.width(w);
            }
            $el.appendTo($rating);
        },
        _getCaption: function () {
            var self = this;
            return self.$caption && self.$caption.length ? self.$caption.html() : self.defaultCaption;
        },
        _setCaption: function (content) {
            var self = this;
            if (self.$caption && self.$caption.length) {
                self.$caption.html(content);
            }
        },
        _renderCaption: function () {
            var self = this, val = self.$element.val(), html, $cap = self.captionElement ? $(self.captionElement) : '';
            if (!self.showCaption) {
                return;
            }
            html = self.fetchCaption(val);
            if ($cap && $cap.length) {
                $h.addCss($cap, 'caption');
                $cap.html(html);
                self.$caption = $cap;
                return;
            }
            self._addContent('caption', '<div class="caption">' + html + '</div>');
            self.$caption = self.$container.find(".caption");
        },
        _renderClear: function () {
            var self = this, css, $clr = self.clearElement ? $(self.clearElement) : '';
            if (!self.showClear) {
                return;
            }
            css = self._getClearClass();
            if ($clr.length) {
                $h.addCss($clr, css);
                $clr.attr({"title": self.clearButtonTitle}).html(self.clearButton);
                self.$clear = $clr;
                return;
            }
            self._addContent('clear',
                '<div class="' + css + '" title="' + self.clearButtonTitle + '">' + self.clearButton + '</div>');
            self.$clear = self.$container.find('.' + self.clearButtonBaseClass);
        },
        _getClearClass: function () {
            var self = this;
            return self.clearButtonBaseClass + ' ' + (self.inactive ? '' : self.clearButtonActiveClass);
        },
        _toggleHover: function (out) {
            var self = this, w, width, caption;
            if (!out) {
                return;
            }
            if (self.hoverChangeStars) {
                w = self.getWidthFromValue(self.clearValue);
                width = out.val <= self.clearValue ? w + '%' : out.width;
                self.$filledStars.css('width', width);
            }
            if (self.hoverChangeCaption) {
                caption = out.val <= self.clearValue ? self.fetchCaption(self.clearValue) : out.caption;
                if (caption) {
                    self._setCaption(caption + '');
                }
            }
        },
        _init: function (options) {
            var self = this, $el = self.$element.addClass('rating-input'), v;
            self.options = options;
            $.each(options, function (key, value) {
                self[key] = value;
            });
            if (self.rtl || $el.attr('dir') === 'rtl') {
                self.rtl = true;
                $el.attr('dir', 'rtl');
            }
            self.starClicked = false;
            self.clearClicked = false;
            self._initSlider(options);
            self._checkDisabled();
            if (self.displayOnly) {
                self.inactive = true;
                self.showClear = false;
                self.hoverEnabled = false;
                self.hoverChangeCaption = false;
                self.hoverChangeStars = false;
            }
            self._generateRating();
            self._initEvents();
            self._listen();
            v = self._parseValue($el.val());
            $el.val(v);
            return $el.removeClass('rating-loading');
        },
        _initCaptionTitle: function() {
            var self = this, caption;
            if (self.showCaptionAsTitle) {
                caption = self.fetchCaption(self.$element.val());
                self.$rating.attr('title', $(caption).text());
            }
        },
        _trigChange: function(params) {
            var self = this;
            self._initCaptionTitle();
            self.$element.trigger('change').trigger('rating:change', params);
        },
        _initEvents: function () {
            var self = this;
            self.events = {
                _getTouchPosition: function (e) {
                    var pageX = $h.isEmpty(e.pageX) ? e.originalEvent.touches[0].pageX : e.pageX;
                    return pageX - self.$rating.offset().left;
                },
                _listenClick: function (e, callback) {
                    e.stopPropagation();
                    e.preventDefault();
                    if (e.handled !== true) {
                        callback(e);
                        e.handled = true;
                    } else {
                        return false;
                    }
                },
                _noMouseAction: function (e) {
                    return !self.hoverEnabled || self.inactive || (e && e.isDefaultPrevented());
                },
                initTouch: function (e) {
                    //noinspection JSUnresolvedVariable
                    var ev, touches, pos, out, caption, w, width, params, clrVal = self.clearValue || 0,
                        isTouchCapable = 'ontouchstart' in window ||
                            (window.DocumentTouch && document instanceof window.DocumentTouch);
                    if (!isTouchCapable || self.inactive) {
                        return;
                    }
                    ev = e.originalEvent;
                    //noinspection JSUnresolvedVariable
                    touches = !$h.isEmpty(ev.touches) ? ev.touches : ev.changedTouches;
                    pos = self.events._getTouchPosition(touches[0]);
                    if (e.type === "touchend") {
                        self._setStars(pos);
                        params = [self.$element.val(), self._getCaption()];
                        self._trigChange(params);
                        self.starClicked = true;
                    } else {
                        out = self.calculate(pos);
                        caption = out.val <= clrVal ? self.fetchCaption(clrVal) : out.caption;
                        w = self.getWidthFromValue(clrVal);
                        width = out.val <= clrVal ? w + '%' : out.width;
                        self._setCaption(caption);
                        self.$filledStars.css('width', width);
                    }
                },
                starClick: function (e) {
                    var pos, params;
                    self.events._listenClick(e, function (ev) {
                        if (self.inactive) {
                            return false;
                        }
                        pos = self.events._getTouchPosition(ev);
                        self._setStars(pos);
                        params = [self.$element.val(), self._getCaption()];
                        self._trigChange(params);
                        self.starClicked = true;
                    });
                },
                clearClick: function (e) {
                    self.events._listenClick(e, function () {
                        if (!self.inactive) {
                            self.clear();
                            self.clearClicked = true;
                        }
                    });
                },
                starMouseMove: function (e) {
                    var pos, out;
                    if (self.events._noMouseAction(e)) {
                        return;
                    }
                    self.starClicked = false;
                    pos = self.events._getTouchPosition(e);
                    out = self.calculate(pos);
                    self._toggleHover(out);
                    self.$element.trigger('rating:hover', [out.val, out.caption, 'stars']);
                },
                starMouseLeave: function (e) {
                    var out;
                    if (self.events._noMouseAction(e) || self.starClicked) {
                        return;
                    }
                    out = self.cache;
                    self._toggleHover(out);
                    self.$element.trigger('rating:hoverleave', ['stars']);
                },
                clearMouseMove: function (e) {
                    var caption, val, width, out;
                    if (self.events._noMouseAction(e) || !self.hoverOnClear) {
                        return;
                    }
                    self.clearClicked = false;
                    caption = '<span class="' + self.clearCaptionClass + '">' + self.clearCaption + '</span>';
                    val = self.clearValue;
                    width = self.getWidthFromValue(val) || 0;
                    out = {caption: caption, width: width, val: val};
                    self._toggleHover(out);
                    self.$element.trigger('rating:hover', [val, caption, 'clear']);
                },
                clearMouseLeave: function (e) {
                    var out;
                    if (self.events._noMouseAction(e) || self.clearClicked || !self.hoverOnClear) {
                        return;
                    }
                    out = self.cache;
                    self._toggleHover(out);
                    self.$element.trigger('rating:hoverleave', ['clear']);
                },
                resetForm: function (e) {
                    if (e && e.isDefaultPrevented()) {
                        return;
                    }
                    if (!self.inactive) {
                        self.reset();
                    }
                }
            };
        },
        _listen: function () {
            var self = this, $el = self.$element, $form = $el.closest('form'), $rating = self.$rating,
                $clear = self.$clear, events = self.events;
            $h.handler($rating, 'touchstart touchmove touchend', $.proxy(events.initTouch, self));
            $h.handler($rating, 'click touchstart', $.proxy(events.starClick, self));
            $h.handler($rating, 'mousemove', $.proxy(events.starMouseMove, self));
            $h.handler($rating, 'mouseleave', $.proxy(events.starMouseLeave, self));
            if (self.showClear && $clear.length) {
                $h.handler($clear, 'click touchstart', $.proxy(events.clearClick, self));
                $h.handler($clear, 'mousemove', $.proxy(events.clearMouseMove, self));
                $h.handler($clear, 'mouseleave', $.proxy(events.clearMouseLeave, self));
            }
            if ($form.length) {
                $h.handler($form, 'reset', $.proxy(events.resetForm, self), true);
            }
            return $el;
        },
        _getStars: function (type) {
            var self = this, stars = '<span class="' + type + '-stars">', i;
            for (i = 1; i <= self.stars; i++) {
                stars += '<span class="star">' + self[type + 'Star'] + '</span>';
            }
            return stars + '</span>';
        },
        _setStars: function (pos) {
            var self = this, out = arguments.length ? self.calculate(pos) : self.calculate(), $el = self.$element,
                v = self._parseValue(out.val);
            $el.val(v);
            self.$filledStars.css('width', out.width);
            self._setCaption(out.caption);
            self.cache = out;
            return $el;
        },
        showStars: function (val) {
            var self = this, v = self._parseValue(val);
            self.$element.val(v);
            self._initCaptionTitle();
            return self._setStars();
        },
        calculate: function (pos) {
            var self = this, defaultVal = $h.isEmpty(self.$element.val()) ? 0 : self.$element.val(),
                val = arguments.length ? self.getValueFromPosition(pos) : defaultVal,
                caption = self.fetchCaption(val), width = self.getWidthFromValue(val);
            width += '%';
            return {caption: caption, width: width, val: val};
        },
        getValueFromPosition: function (pos) {
            var self = this, precision = $h.getDecimalPlaces(self.step), val, factor, maxWidth = self.$rating.width();
            factor = (self.diff * pos) / (maxWidth * self.step);
            factor = self.rtl ? Math.floor(factor) : Math.ceil(factor);
            val = $h.applyPrecision(parseFloat(self.min + factor * self.step), precision);
            val = Math.max(Math.min(val, self.max), self.min);
            return self.rtl ? (self.max - val) : val;
        },
        getWidthFromValue: function (val) {
            var self = this, min = self.min, max = self.max, factor, $r = self.$emptyStars, w;
            if (!val || val <= min || min === max) {
                return 0;
            }
            w = $r.outerWidth();
            factor = w ? $r.width() / w : 1;
            if (val >= max) {
                return 100;
            }
            return (val - min) * factor * 100 / (max - min);
        },
        fetchCaption: function (rating) {
            var self = this, val = parseFloat(rating) || self.clearValue, css, cap, capVal, cssVal, caption,
                vCap = self.starCaptions, vCss = self.starCaptionClasses, width = self.getWidthFromValue(val);
            if (val && val !== self.clearValue) {
                val = $h.applyPrecision(val, $h.getDecimalPlaces(self.step));
            }
            cssVal = typeof vCss === "function" ? vCss(val, width) : vCss[val];
            capVal = typeof vCap === "function" ? vCap(val, width) : vCap[val];

            cap = $h.isEmpty(capVal) ? self.defaultCaption.replace(/\{rating}/g, val) : capVal;
            css = $h.isEmpty(cssVal) ? self.clearCaptionClass : cssVal;
            caption = (val === self.clearValue) ? self.clearCaption : cap;
            return '<span class="' + css + '">' + caption + '</span>';
        },
        destroy: function () {
            var self = this, $el = self.$element;
            if (!$h.isEmpty(self.$container)) {
                self.$container.before($el).remove();
            }
            $.removeData($el.get(0));
            return $el.off('rating').removeClass('rating rating-input');
        },
        create: function (options) {
            var self = this, opts = options || self.options || {};
            return self.destroy().rating(opts);
        },
        clear: function () {
            var self = this, title = '<span class="' + self.clearCaptionClass + '">' + self.clearCaption + '</span>';
            if (!self.inactive) {
                self._setCaption(title);
            }
            return self.showStars(self.clearValue).trigger('change').trigger('rating:clear');
        },
        reset: function () {
            var self = this;
            return self.showStars(self.initialValue).trigger('rating:reset');
        },
        update: function (val) {
            var self = this;
            return arguments.length ? self.showStars(val) : self.$element;
        },
        refresh: function (options) {
            var self = this, $el = self.$element;
            if (!options) {
                return $el;
            }
            return self.destroy().rating($.extend(true, self.options, options)).trigger('rating:refresh');
        }
    };

    $.fn.rating = function (option) {
        var args = Array.apply(null, arguments), retvals = [];
        args.shift();
        this.each(function () {
            var self = $(this), data = self.data('rating'), options = typeof option === 'object' && option,
                theme = options.theme || self.data('theme'), lang = options.language || self.data('language') || 'en',
                thm = {}, loc = {}, opts;
            if (!data) {
                if (theme) {
                    thm = $.fn.ratingThemes[theme] || {};
                }
                if (lang !== 'en' && !$h.isEmpty($.fn.ratingLocales[lang])) {
                    loc = $.fn.ratingLocales[lang];
                }
                opts = $.extend(true, {}, $.fn.rating.defaults, thm, $.fn.ratingLocales.en, loc, options, self.data());
                data = new Rating(this, opts);
                self.data('rating', data);
            }

            if (typeof option === 'string') {
                retvals.push(data[option].apply(data, args));
            }
        });
        switch (retvals.length) {
            case 0:
                return this;
            case 1:
                return retvals[0] === undefined ? this : retvals[0];
            default:
                return retvals;
        }
    };

    $.fn.rating.defaults = {
        theme: '',
        language: 'en',
        stars: 3,
        filledStar: '<i class="fa fa-leaf"></i>',
        emptyStar: '<i class="fa fa-leaf"></i>',
        containerClass: '',
        size: 'md',
        animate: true,
        displayOnly: false,
        rtl: false,
        showClear: true,
        showCaption: true,
        starCaptionClasses: {
            0.5: 'label label-danger badge-danger',
            1: 'label label-danger badge-danger',
            1.5: 'label label-warning badge-warning',
            2: 'label label-warning badge-warning',
            2.5: 'label label-info badge-info',
            3: 'label label-info badge-info',
            3.5: 'label label-primary badge-primary',
            4: 'label label-primary badge-primary',
            4.5: 'label label-success badge-success',
            5: 'label label-success badge-success'
        },
        clearButton: '<i class="glyphicon glyphicon-minus-sign"></i>',
        clearButtonBaseClass: 'clear-rating',
        clearButtonActiveClass: 'clear-rating-active',
        clearCaptionClass: 'label label-default badge-secondary',
        clearValue: null,
        captionElement: null,
        clearElement: null,
        showCaptionAsTitle: true,
        hoverEnabled: true,
        hoverChangeCaption: true,
        hoverChangeStars: true,
        hoverOnClear: true,
        zeroAsNull: true
    };

    $.fn.ratingLocales.en = {
        defaultCaption: '{rating} Stars',
        starCaptions: {
            0.5: 'Half Star',
            1: 'One Star',
            1.5: 'One & Half Star',
            2: 'Two Stars',
            2.5: 'Two & Half Stars',
            3: 'Three Stars',
            3.5: 'Three & Half Stars',
            4: 'Four Stars',
            4.5: 'Four & Half Stars',
            5: 'Five Stars'
        },
        clearButtonTitle: 'Clear',
        clearCaption: ''
    };

    $.fn.rating.Constructor = Rating;

    /**
     * Convert automatically inputs with class 'rating' into Krajee's star rating control.
     */
    $(document).ready(function () {
        var $input = $('input.rating');
        if ($input.length) {
            $input.removeClass('rating-loading').addClass('rating-loading').rating();
        }
    });
}));

$(function() {
    $(".pippo").rating({
        min: 0,
        max: 4,
        step: 0.1,
        size: 'lg',
        showClear: false,
        disabled: true,
        starCaptions: function (val) {
            return val;
        },
        starCaptionClasses: function (val) {
            if (val < 3) {
                return 'label label-danger';
            } else {
                return 'label label-success';
            }
        },
        hoverOnClear: false
    });
    $(".pippoB").rating({
        min: 0,
        max: 4,
        step: 0.1,
        size: 'lg',
        showClear: false,
        disabled: true,
        starCaptions: function (val) {
            return val;
        },
        starCaptionClasses: function (val) {
            if (val < 3) {
                return 'label label-danger';
            } else {
                return 'label label-success';
            }
        },
        hoverOnClear: false
    });
});


$(document).on('change','.valuesSelect', function(){
    $("#reverseSpinnerMatrixA").removeClass("d-none");
    var selectedValues = [];
    $(".valuesSelect").each(function() {
        var selectedValue = $(this).children("option:selected").val();
        if(selectedValue !== "Select a value") {
            selectedValues.push(selectedValue);
        }
        console.log("You have selected the country - " + selectedValue);
    });

    $.ajax({
        url: window.location.pathname + "/getScores",
        type: "POST",
        data: {selectedValues: selectedValues},
        traditional: true,
        cache: false,
        timeout: 600000,
        success: function (data) {
            $("#resultsFragment").html(data);
            $("#resultsFragment").fadeIn(3000);
            $("#reverseSpinnerMatrixA").addClass("d-none");
            /*$(".pippo").rating({
                min: 0,
                max: 4,
                step: 0.1,
                size: 'lg',
                showClear: false,
                disabled: true,
                starCaptions: function (val) {
                    return val;
                },
                starCaptionClasses: function (val) {
                    if (val < 3) {
                        return 'label label-danger';
                    } else {
                        return 'label label-success';
                    }
                },
                hoverOnClear: false
            });

            $(".partialResults").each(function() {
                $(this).removeClass("d-none");
            });*/

        },
        error: function (data) {
            console.log(data.size);
        }
    });
});

$(document).on('change','.valuesBSelect', function() {
    console.log("ENTRO IN B " + window.location.pathname);
    var selectedValues = [];
    $(".valuesBSelect").each(function() {
        var selectedValue = $(this).children("option:selected").val();
        if(selectedValue !== "Select a value") {
            selectedValues.push(selectedValue);
        }
        console.log("You have selected the country - " + selectedValue);
    });
    console.log(selectedValues);
    $.ajax({
        url: window.location.pathname + "getScoresB",
        type: "POST",
        data: {selectedValues: selectedValues},
        traditional: true,
        cache: false,
        timeout: 600000,
        success: function (data) {
            $("#resultsBFragment").html(data);
            $(".pippoB").rating({
                min: 0,
                max: 4,
                step: 0.1,
                size: 'lg',
                showClear: false,
                disabled: true,
                starCaptions: function (val) {
                    return val;
                },
                starCaptionClasses: function (val) {
                    if (val < 3) {
                        return 'label label-danger';
                    } else {
                        return 'label label-success';
                    }
                },
                hoverOnClear: false
            });

            $(".partialBResults").each(function() {
                $(this).removeClass("d-none");
            });

        },
        error: function (data) {
            console.log(data.size);
        }
    });
});

$(document).delegate(".tabManager", "click", function (event) {
    var anchor = $(event.target).is('a') ? $(event.target) : $(event.target).parent();
    var tabId = anchor.attr('href').replace("#", "");
    if($('#'+tabId).attr("class").indexOf("d-none") > 0) {
        $('#'+tabId).removeClass("d-none");
        anchor.find('i').attr("class", "fa fa-eye-slash");
    }
    else {
        $('#'+tabId).addClass("d-none");
        anchor.find('i').attr("class", "fa fa-eye");
    }
});

$(document).delegate(".tabManagerB", "click", function (event) {
    var anchor = $(event.target).is('a') ? $(event.target) : $(event.target).parent();
    console.log(anchor.attr("class"));
    var tabId = anchor.attr('href').replace("#", "");
    console.log(tabId);
    if($('#'+tabId).attr("class").indexOf("d-none") > 0) {
        $('#'+tabId).removeClass("d-none");
        anchor.find('i').attr("class", "fa fa-eye-slash");
    }
    else {
        $('#'+tabId).addClass("d-none");
        anchor.find('i').attr("class", "fa fa-eye");
    }
});

$(document).delegate(".demo__hover", "click", function(event) {
    console.log($(event.target).attr("class"));
    var id = $(event.target).attr("class").replace('demo__hover demo__hover-', '');
    console.log(id);
    var checkSquareIcon = $(".demo__elem-" + id).find('i.fa-check-square');
    var squareIcon = $(".demo__elem-" + id).find('i.fa-square');
    if(checkSquareIcon.attr("class").indexOf('d-none') === -1) {
        checkSquareIcon.addClass("d-none");
        squareIcon.removeClass("d-none");
    }
    else {
        checkSquareIcon.removeClass("d-none");
        squareIcon.addClass("d-none");
    }
});

$(document).delegate(".step2NextButton", "click", function(event) {
    var results = [];
    $('.demo__elem').each(function(i, obj) {
        var icon = $(obj).find('i.fa-check-square');
        if(icon.attr("class").indexOf('d-none') === -1) {
            console.log($(obj).html());
            console.log($(obj).text());
            results.push($(obj).text());
        }
    });
    console.log(results);
    $.ajax({
        type: "POST",
        url: "/biovoicesMtool/step2Next",
        traditional: true,
        data: {results: results},
        cache: false,
        timeout: 600000,
        success: function (data) {
            window.location.href = '/biovoicesMtool/chooseGroupSize';
        },
        error: function (e) {
        }
    });
});

$(document).delegate(".viewAllSuggestionsButton", "click", function(event) {
    $('.demo__elem').each(function(i, obj) {
        if($(obj).attr("class").indexOf("d-none") !== -1) {
            $(obj).removeClass("d-none");
        }
    });
    $('.mediumScore').each(function(i, obj) {
        if($(obj).attr("class").indexOf("d-none") !== -1) {
            $(obj).removeClass("d-none");
        }
    });
    $(".viewAllSuggestionsButton").addClass("hideAllSuggestionsButton");
    $(".hideAllSuggestionsButton").removeClass("viewAllSuggestionsButton");
    $(".hideAllSuggestionsButton").text("HIDE ALL SUGGESTIONS BUTTON");
});

$(document).delegate(".hideAllSuggestionsButton", "click", function(event) {
    $('.demo__elem').each(function(i, obj) {
        if($(obj).attr("class").indexOf("d-none") === -1 && i > 2) {
            $(obj).addClass("d-none");
        }
    });
    $('.mediumScore').each(function(i, obj) {
        if($(obj).attr("class").indexOf("d-none") === -1) {
            $(obj).addClass("d-none");
        }
    });
    $(".hideAllSuggestionsButton").addClass("viewAllSuggestionsButton");
    $(".viewAllSuggestionsButton").removeClass("hideAllSuggestionsButton");
    $(".viewAllSuggestionsButton").text("VIEW ALL SUGGESTIONS BUTTON");
});

$(document).delegate(".viewFactSheet", "click", function (event) {
    var mmlName = $(event.target).closest('.demo__elem').find(".mmlToShow").text();
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/biovoicesMtool/viewFactSheet",
        data: {mmlName: mmlName},
        success: function (data) {
            $("#viewMMLFactSheetModal").replaceWith(data);
            $("#viewMMLFactSheetModal").modal("show");
        },
        error: function (e) {
            console.log("error");
        }
    });
});

$(document).delegate(".viewMoreFactSheet", "click", function(event) {
    $('.exampleLinkify').linkify();
    $('.relevantSourcesLinkify').linkify();
    $('.viewMoreFactSheetRow').removeClass("d-none");
    $(this).removeClass('viewMoreFactSheet');
    $(this).addClass('hideMoreFactSheet');
    $(this).text('HIDE')
});

$(document).delegate(".hideMoreFactSheet", "click", function(event) {
    $('.viewMoreFactSheetRow').addClass("d-none");
    $(this).removeClass('hideMoreFactSheet');
    $(this).addClass('viewMoreFactSheet');
    $(this).text('VIEW MORE')
});

var mmlNameChosen;

$(document).delegate(".chooseFactSheet", "click", function (event) {
    var mmlName = $(event.target).closest('.demo__elem').find(".mmlToShow").text();
    $(".mmlChoiceText").text('You are chosing ' + mmlName + '. Are you sure?');
    $("#chooseMMLFactSheetModal").modal("show");
    mmlNameChosen = mmlName;
});

$(document).delegate(".mmlChoiceConfirmed", "click", function (event) {
    $.ajax({
        type: "POST",
        url: "/biovoicesMtool/chooseMml",
        traditional: true,
        data: {mmlName: mmlNameChosen},
        cache: false,
        timeout: 600000,
        success: function (data) {
            window.location.href = '/biovoicesMtool/viewAgenda';
        },
        error: function (e) {
        }
    });
});

$('.draggable').draggable({
    helper: 'clone'
});

function droppable() {
    console.log("ENTRO IN DROPPABLE");
    $( ".droppable" ).droppable({
        accept: '.draggable',
        drop: function(ev, ui) {
            console.log($(this).attr("class"));
            if ($(this).attr("class").indexOf("assigned") === -1) {
                console.log("ENTRO IN primo if");
                if (ui.draggable.attr("class").indexOf("extraActivity") === -1) {
                    console.log("ENTRO IN secondo if");
                    var tr = $(this).parent().clone();
                    $(tr).insertAfter($(this).parent());

                    $(tr).find(".agendaItemName").html('');
                    $(tr).find(".agendaItemDescription").html('');
                    $(tr).find(".agendaItemTime").html('<h5>Insert time</h5>');
                    console.log($(tr).attr("class"));
                    console.log($(tr).html());
                    if ($(tr).attr("class").indexOf("clone") !== -1) {
                        $(this).parent().find(".deleteIcon").html('<i style="color: #7f7f7f" class="fas fa-times fa-2x deleteRow"/>')
                    }
                    $(tr).addClass("clone");
                    $(this).html(ui.draggable.clone());
                    $(this).addClass("assigned");
                } else {
                    console.log("ENTRO IN ELSE");
                    var tr = $(this).parent().clone();
                    $(tr).insertBefore($(this).parent());

                    $(tr).find(".agendaItemName").html('<h5>' + ui.draggable.html() + '</h5>');
                    $(tr).find(".agendaItemDescription").html('');
                    $(tr).find(".agendaItemTime").html('<h5>Insert time</h5>');
                    $(tr).find(".droppable").html('');
                    $(tr).find(".deleteIcon").html('<i style="color: #FFFFFF" class="fas fa-times fa-2x deleteRow"/>')
                    $(tr).css('background-color', 'rgb(43, 150, 78)');
                    $(tr).css('opacity', '0.5');
                    $(tr).css('color', '#FFFFFF');
                    $(tr).find(".droppable").addClass("assigned");
                    $(this).html('<h5>Drag an activity</h5>');
                }
                droppable();
            } else {
                console.log("ULTIMO ELSE");
                $(tr).find(".deleteIcon").html('<i style="color: #7f7f7f" class="fas fa-times fa-2x deleteRow"/>')
                $(this).html(ui.draggable.clone());
            }
        }
    });
}

function checkSaveElements() {

    $( ".saved" ).each(function( index ) {
        console.log($(this));
        // DUE FILE INIZIALI CONSECUTIVE
        if(rowCounter($(this)) === 4 && rowCounter($(this).next()) === 4 && $(this).find(".droppable").html().indexOf("Drag") === -1) {
            console.log("1");
            var tr = $(this).clone();

            $(tr).find(".agendaItemName").html('');
            $(tr).find(".agendaItemDescription").html('');
            $(tr).find(".agendaItemTime").html('<h5>Insert time</h5>');
            $(tr).find(".droppable").html('<h5 style="border: 2px solid #2b964e" class="p-2" >Drag an activity</h5>');
            $(tr).find(".droppable").removeClass("assigned");
            $(tr).attr("style", "");
            $(tr).find(".deleteIcon").html('');
            $(tr).addClass("clone");
            $(tr).insertAfter($(this));
            droppable();
        }
        if(rowCounter($(this)) === 4 && rowCounter($(this).next()) === 4 && $(this).find(".droppable").html().indexOf("Drag") !== -1 &&
            $(this).next().find(".droppable").html().indexOf("Drag") !== -1) {

            console.log("2");

            $(this).find(".droppable").html('<h5 style="border: 2px solid #2b964e" class="p-2" >Drag an activity</h5>');
            $(this).find(".droppable").removeClass("assigned");
            $(this).find(".deleteIcon").html('');
            $(this).next().find(".droppable").html('<h5 style="border: 2px solid #2b964e" class="p-2" >Drag an activity</h5>');
            $(this).next().find(".droppable").removeClass("assigned");
            $(tr).attr("style", "");
            $(this).next().find(".deleteIcon").html('');
            droppable();
        }
        // UNA FILA DA DUE + ICONA E UNA FILA DA 4
        if(rowCounter($(this)) === 3 && rowCounter($(this).next()) === 4 && $(this).find(".droppable").html().indexOf("Drag") === -1) {

            console.log("3");

            var tr = $(this).clone();
            var tr4 = $(this).next();

            $(tr).find(".agendaItemName").html('');
            $(tr).find(".agendaItemDescription").html('');
            $(tr).find(".agendaItemTime").html('<h5>Insert time</h5>');
            $(tr).find(".droppable").html('<h5 style="border: 2px solid #2b964e" class="p-2" >Drag an activity</h5>');
            $(tr).find(".droppable").removeClass("assigned");
            $(tr).attr("style", "");
            $(tr).addClass("clone");
            $(tr).find(".deleteIcon").html('');
            $(tr).insertAfter($(this));

            if(tr4.find(".droppable").html().indexOf("Drag") !== -1) {
                tr4.find(".droppable").html('<h5 style="border: 2px solid #2b964e" class="p-2" >Drag an activity</h5>');
                tr4.find(".droppable").removeClass("assigned");
                tr4.find(".deleteIcon").html('');
                tr4.attr("style", "");
            }

            droppable();
        }

        if(rowCounter($(this)) === 4 && rowCounter($(this).next()) === 4 && $(this).find(".droppable").html().indexOf("Drag") !== -1) {

            console.log("4");

            $(this).find(".droppable").html('<h5 style="border: 2px solid #2b964e" class="p-2" >Drag an activity</h5>');
            $(this).find(".droppable").removeClass("assigned");
            $(tr).attr("style", "");
            $(this).find(".deleteIcon").html('');
            droppable();
        }

        if(rowCounter($(this)) === 3 && rowCounter($(this).next()) === 1) {

            console.log("5");

            var tr = $(this).clone();

            $(tr).find(".agendaItemName").html('');
            $(tr).find(".agendaItemDescription").html('');
            $(tr).find(".agendaItemTime").html('<h5>Insert time</h5>');
            $(tr).find(".droppable").html('<h5 style="border: 2px solid #2b964e" class="p-2" >Drag an activity</h5>');
            $(tr).find(".droppable").removeClass("assigned");
            $(tr).attr("style", "");
            $(tr).find(".deleteIcon").html('');
            $(tr).addClass("clone");
            $(tr).insertAfter($(this));
            droppable();
        }

        if(rowCounter($(this)) === 4 && rowCounter($(this).next()) === 1 && $(this).find(".droppable").html().indexOf("Drag") === -1) {

            console.log("6");

            var tr = $(this).clone();

            $(tr).find(".agendaItemName").html('');
            $(tr).find(".agendaItemDescription").html('');
            $(tr).find(".agendaItemTime").html('<h5>Insert time</h5>');
            $(tr).find(".droppable").html('<h5 style="border: 2px solid #2b964e" class="p-2" >Drag an activity</h5>');
            $(tr).find(".droppable").removeClass("assigned");
            $(tr).attr("style", "");
            $(tr).find(".deleteIcon").html('');
            $(tr).addClass("clone");
            $(tr).insertAfter($(this));
            droppable();
        }

        if(rowCounter($(this)) === 4 && rowCounter($(this).next()) === 0 && $(this).find(".droppable").html().indexOf("Drag") === -1) {

            console.log("7");

            var tr = $(this).clone();

            $(tr).find(".agendaItemName").html('');
            $(tr).find(".agendaItemDescription").html('');
            $(tr).find(".agendaItemTime").html('<h5>Insert time</h5>');
            $(tr).find(".droppable").html('<h5 style="border: 2px solid #2b964e" class="p-2" >Drag an activity</h5>');
            $(tr).find(".droppable").removeClass("assigned");
            $(tr).attr("style", "");
            $(tr).find(".deleteIcon").html('');
            $(tr).addClass("clone");
            $(tr).insertAfter($(this));
            droppable();
        }

        if(rowCounter($(this)) === 3 && rowCounter($(this).next()) === 0 && $(this).find(".droppable").html().indexOf("Drag") === -1) {

            console.log("8");

            var tr = $(this).clone();

            $(tr).find(".agendaItemName").html('');
            $(tr).find(".agendaItemDescription").html('');
            $(tr).find(".agendaItemTime").html('<h5>Insert time</h5>');
            $(tr).find(".droppable").html('<h5 style="border: 2px solid #2b964e" class="p-2" >Drag an activity</h5>');
            $(tr).find(".droppable").removeClass("assigned");
            $(tr).attr("style", "");
            $(tr).addClass("clone");
            $(tr).find(".deleteIcon").html('');
            $(tr).insertAfter($(this));

            droppable();
        }


    });
}

function rowCounter(tr) {
    var counter = 0;
    $(tr).find('td').each (function() {
        if($(this).html() !== "") {
            counter++;
        }
    });
    return counter;
}

$(function () {
    checkSaveElements();
    droppable();
});

$(document).delegate(".viewAllActivitiesSuggestionsButton", "click", function(event) {
    $('.whiteBackgroundElem').each(function(i, obj) {
        if($(obj).attr("class").indexOf("d-none") !== -1) {
            $(obj).removeClass("d-none");
        }
    });
    $('.mediumScore').each(function(i, obj) {
        if($(obj).attr("class").indexOf("d-none") !== -1) {
            $(obj).removeClass("d-none");
        }
    });
    $(".viewAllActivitiesSuggestionsButton").addClass("hideAllActivitiesSuggestionsButton");
    $(".hideAllActivitiesSuggestionsButton").removeClass("viewAllActivitiesSuggestionsButton");
    $(".hideAllActivitiesSuggestionsButton").text("HIDE ALL SUGGESTIONS BUTTON");
});

$(document).delegate(".hideAllActivitiesSuggestionsButton", "click", function(event) {
    $('.whiteBackgroundElem').each(function(i, obj) {
        if($(obj).attr("class").indexOf("top3Result") === -1 && $(obj).attr("class").indexOf("agendaPhaseHeading") === -1) {
            $(obj).addClass("d-none");
        }
    });
    $('.mediumScore').each(function(i, obj) {
        if($(obj).attr("class").indexOf("d-none") !== -1) {
            $(obj).addClass("d-none");
        }
    });
    $(".hideAllActivitiesSuggestionsButton").addClass("viewAllActivitiesSuggestionsButton");
    $(".viewAllActivitiesSuggestionsButton").removeClass("hideAllActivitiesSuggestionsButton");
    $(".viewAllActivitiesSuggestionsButton").text("VIEW ALL SUGGESTIONS BUTTON");
});

$(document).delegate(".deleteRow", "click", function() {
    $(this).parent().parent().remove();
});

$(document).delegate(".viewActivityFactSheet", "click", function (event) {
    var activityName = $(event.target).closest('.whiteBackgroundElem').find(".activityToShow").text();
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/biovoicesMtool/viewActivityFactSheet",
        data: {activityName: activityName},
        success: function (data) {
            $("#viewActivityFactSheetModal").replaceWith(data);
            $("#viewActivityFactSheetModal").modal("show");
        },
        error: function (e) {
            console.log("error");
        }
    });
});

$(document).delegate(".viewMoreActivityFactSheet", "click", function(event) {
    $('.exampleLinkify').linkify();
    $('.relevantSourcesLinkify').linkify();
    $('.viewMoreFactSheetRow').removeClass("d-none");
    $(this).removeClass('viewMoreActivityFactSheet');
    $(this).addClass('hideMoreActivityFactSheet');
    $(this).text('HIDE')
});

$(document).delegate(".hideMoreActivityFactSheet", "click", function(event) {
    $('.viewMoreFactSheetRow').addClass("d-none");
    $(this).removeClass('hideMoreActivityFactSheet');
    $(this).addClass('viewMoreActivityFactSheet');
    $(this).text('VIEW MORE')
});

$(document).delegate(".imDoneWithAgendaCustomizationSettingSceneButton", "click", function (event) {
    var rows = [];
    var theadArray = [];
    var tBodyArray = [];

    $('#customizeAgendaTable > thead > tr > th > h5').each(function(index, element) {
        theadArray.push($(element).html());
    });
    rows.push(theadArray);

    $('#customizeAgendaTable > tbody > tr').each(function(index, element) {
        $(element).find('td') .each(function(index, element2) {
            var text = $(element2).find("h5").html();
            if(text !== undefined) {
                console.log(text);
                text = text.replace(/,/g, "*$");
                tBodyArray.push(text);
            }
        });
        rows.push(tBodyArray);
        tBodyArray = [];
    });

    $.ajax({
        type: "POST",
        url: "/biovoicesMtool/agendaCustomizationSessionFormatEnd",
        traditional: true,
        data: {rows: rows},
        cache: false,
        timeout: 600000,
        success: function (data) {
            window.location.href = '/biovoicesMtool/customizeAgendaWorkingPhase';
        },
        error: function (e) {
        }
    });
});

$(document).delegate(".imDoneWithAgendaCustomizationWorkingPhaseButton", "click", function (event) {
    var rows = [];
    var theadArray = [];
    var tBodyArray = [];

    $('#customizeAgendaTable > thead > tr > th > h5').each(function(index, element) {
        theadArray.push($(element).html());
    });
    rows.push(theadArray);

    $('#customizeAgendaTable > tbody > tr').each(function(index, element) {
        if($(this).attr('class').indexOf('greyRow') === -1) {
            $(element).find('td') .each(function(index, element2) {
                var text = $(element2).find("h5").html();
                if(text !== undefined) {
                    console.log(text);
                    text = text.replace(/,/g, "*$");
                    tBodyArray.push(text);
                }
            });
            rows.push(tBodyArray);
            tBodyArray = [];
        }
    });

    $.ajax({
        type: "POST",
        url: "/biovoicesMtool/agendaCustomizationWorkingPhaseEnd",
        traditional: true,
        data: {rows: rows},
        cache: false,
        timeout: 600000,
        success: function (data) {
            window.location.href = '/biovoicesMtool/customizeAgendaWrapUp';
        },
        error: function (e) {
        }
    });
});

$(document).delegate(".imDoneWithAgendaCustomizationWrapUpButton", "click", function (event) {
    var rows = [];
    var theadArray = [];
    var tBodyArray = [];

    var newActivitiesOnly = [];

    $('#customizeAgendaTable > thead > tr > th > h5').each(function(index, element) {
        theadArray.push($(element).html());
    });
    rows.push(theadArray);

    $('#customizeAgendaTable > tbody > tr').each(function(index, element) {
            $(element).find('td') .each(function(index, element2) {
                var text = $(element2).find("h5").html();
                if(text !== undefined) {
                    console.log(text);
                    text = text.replace(/,/g, "*$");
                    tBodyArray.push(text);
                }
            });
            rows.push(tBodyArray);
            tBodyArray = [];
    });
    $('#customizeAgendaTable > tbody > tr').each(function(index, element) {
        if ($(this).attr('class').indexOf('greyRow') === -1) {
            $(element).find('td').each(function (index, element2) {
                var text = $(element2).find("h5").html();
                if (text !== undefined) {
                    console.log(text);
                    text = text.replace(/,/g, "*$");
                    tBodyArray.push(text);
                }
            });
            newActivitiesOnly.push(tBodyArray);
            tBodyArray = [];
        }
    });
 console.log("new act only " + newActivitiesOnly);
    $.ajax({
        type: "POST",
        url: "/biovoicesMtool/agendaCustomizationWrapUpEnd",
        traditional: true,
        data: {rows: rows, newActivitiesOnly: newActivitiesOnly},
        cache: false,
        timeout: 600000,
        success: function (data) {
            window.location.href = '/biovoicesMtool/downloadSupportingDocuments';
        },
        error: function (e) {
        }
    });
});

$(document).delegate(".viewActivityFactSheetSummary", "click", function (event) {
    var activityName = $(event.target).attr("id");
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/biovoicesMtool/viewActivityFactSheet",
        data: {activityName: activityName},
        success: function (data) {
            $("#viewActivityFactSheetModal").replaceWith(data);
            $("#viewActivityFactSheetModal").modal("show");
        },
        error: function (e) {
            console.log("error");
        }
    });
});

$(document).delegate(".viewFactSheetSummary", "click", function (event) {
    var mmlName = $(event.target).attr("id");
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/biovoicesMtool/viewFactSheet",
        data: {mmlName: mmlName},
        success: function (data) {
            $("#viewMMLFactSheetModal").replaceWith(data);
            $("#viewMMLFactSheetModal").modal("show");
        },
        error: function (e) {
            console.log("error");
        }
    });
});






