package com.hcdc.xncovid.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KBYTRegex {

    private String outputKey;

    private ArrayList<RegexObj> patternObjs;

    private ArrayList<String> outContent;

    public String getOutputKey() {
        return outputKey;
    }

    public void setOutputKey(String outputKey) {
        this.outputKey = outputKey;
    }

    public ArrayList<RegexObj> getPatternObjs() {
        return patternObjs;
    }

    public void setPatternObjs(ArrayList<RegexObj> patternObjs) {
        this.patternObjs = patternObjs;
    }

    public ArrayList<String> getOutContent() {
        return outContent;
    }

    public void setOutContent(String outContent) {
        if(this.outContent == null){
            this.outContent = new ArrayList<String>();
        }
         this.outContent.add((outContent));
    }

    public static class RegexObj{
        private String parttern;

        private  String extractKey;

        public RegexObj(String parttern, String extractKey) {
            this.parttern = parttern;
            this.extractKey = extractKey;
        }
    }

    public String ExtractContent(String inputContent){
        String txtContent ="";
        Hashtable<String, String> kv = new Hashtable<>();
        try{
            for (RegexObj item: patternObjs) {
                Pattern p = Pattern.compile(item.parttern);
                Matcher m = p.matcher(inputContent);
                String content = "";
                if(m.find()){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        content  += m.group(item.extractKey);
                    }else {
                        content += m.group(1);
                    }
                }
                kv.put(item.extractKey, content);
            }

            for (String key: outContent) {
                if(key.startsWith("%") && key.endsWith("%")) //param thi ley trong Hash
                {
                    String objKey = key.substring(1,key.length()-2);
                    txtContent += kv.get(objKey);
                    continue;
                }
                txtContent += key;
            }

            return  txtContent;
        }catch (Exception e){
            Log.e("ExtractContent ", "Detect Failed", e);
            return  txtContent;
        }

    }

    public void   addPattern(String pattern, String extractKey){
        RegexObj item = new RegexObj(pattern, extractKey);
        if(patternObjs == null){
            patternObjs = new ArrayList<>();
        }
        patternObjs.add(item);
    }

}
