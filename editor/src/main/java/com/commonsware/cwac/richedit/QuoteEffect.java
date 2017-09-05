/***
 Copyright (c) 2008-2011 CommonsWare, LLC

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.commonsware.cwac.richedit;

import android.text.Spannable;
import android.text.style.QuoteSpan;

import com.commonsware.cwac.richtextutils.Selection;

public class QuoteEffect extends Effect<Boolean> {

    @Override
    public boolean existsInSelection(RichEditText editor) {
        Spannable str = editor.getText();
        Selection selection = new Selection(editor).extendToFullLine(str);

        return (getQuoteSpans(str, selection).length > 0);
    }

    @Override
    public Boolean valueInSelection(RichEditText editor) {
        return (existsInSelection(editor));
    }

    @Override
    public void applyToSelection(RichEditText editor, Boolean add) {
        applyToSelection(editor, new Selection(editor), add);
    }

    void applyToSelection(RichEditText editor, Selection quotes, Boolean add) {
        Spannable str = editor.getText();
        Selection selection = quotes.extendToFullLine(str);

        for (QuoteSpan span : getQuoteSpans(str, selection)) {
            str.removeSpan(span);
        }

        if (add.booleanValue()) {
            for (Selection chunk : selection.buildSelectionsForLines(str)) {
                str.setSpan(new QuoteSpan(), chunk.getStart(),
                        chunk.getEnd(),
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
    }

    private QuoteSpan[] getQuoteSpans(Spannable str, Selection selection) {
        return str.getSpans(selection.getStart(), selection.getEnd(), QuoteSpan.class);
    }
}