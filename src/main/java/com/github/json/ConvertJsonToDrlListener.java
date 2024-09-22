package com.github.json;

import com.github.json.antlr.JSONBaseListener;
import com.github.json.antlr.JSONParser;

public class ConvertJsonToDrlListener extends JSONBaseListener {

    @Override
    public void enterValue(JSONParser.ValueContext ctx) {
        System.out.println(ctx.getText());
    }
}
