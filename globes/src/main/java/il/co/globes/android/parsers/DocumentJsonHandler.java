package il.co.globes.android.parsers;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import il.co.globes.android.AppRegisterForPushApps;
import il.co.globes.android.Utils;
import il.co.globes.android.objects.Document;
import il.co.globes.android.objects.Tagit;
import net.tensera.sdk.api.TenseraApi;
import net.tensera.sdk.api.TenseraResponseStream;
import net.tensera.sdk.fetch.CacheMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DocumentJsonHandler {
    private boolean in_authorIcon = false;
    private Document parsedDocument = new Document();

    public Document getParsedData(String url) throws JSONException {
        this.parsedDocument = new Document();
        this.parsedDocument.setTagiot(new ArrayList<Tagit>());

        //url="http://www.globes.co.il/apps/apps_json.asmx/DocumentJson?doc_id=1000914364&ied=true&from=app_android";

//        httppost.setHeader("Content-type", "application/json");
//        httppost.setHeader("User-agent", "android");

        String result = "";
        if (AppRegisterForPushApps.enableTensera) {
            ArrayMap<String, String> requestHeaders = new ArrayMap<>();
            requestHeaders.put("Content-type", "application/json");
            requestHeaders.put("User-agent", "android");
            TenseraResponseStream tenseraResponseStream = TenseraApi.fetchUrlByPost(url, requestHeaders, CacheMode.CACHE_OTHERWISE_NETWORK);
            if (tenseraResponseStream != null) {
                try {
                    byte[] inputData = tenseraResponseStream.getDataAndCloseStream();
                    result = new String(inputData, "UTF-8");
                } catch (Exception e) {
                    result = "";
                }
            }
        }
        if (result.equals("")) {
            // tensera inactive or failed, use legacy code:
            result = Utils.getJsonData(url);
        }

        //Log.e("alex","ParsingJsonStart_2");
        //Log.e("alex","ParsingJsonURL: " + url);
        //Log.e("alex","sJSon:" + sJSon);

        if (!result.equals("")) {
            try {
                Log.e("alex", "ParsingJsonStart...");
                JSONObject jObject = new JSONObject(result);
                JSONObject jDocumentNode = jObject.getJSONObject("document");

                //Log.e("alex","ParsingJsonStart_14: " + url);

                parsedDocument.setDoc_id(getValue(jDocumentNode, "doc_id", ""));
                //parsedDocument.setModifiedOn(getValue(jDocumentNode, "modified_on", ""));
                parsedDocument.setModifiedOn(getValue(jDocumentNode, "created_on", ""));
                parsedDocument.setDoctype(getValue(jDocumentNode, "doctyp_id", ""));
                parsedDocument.setTitle(getValue(jDocumentNode, "title", ""));
                parsedDocument.setSubTitle(getValue(jDocumentNode, "sub_title", ""));
                parsedDocument.setText(getValue(jDocumentNode, "text", ""));
                parsedDocument.setPlayBuzz(getValue(jDocumentNode, "f86", ""));

                //String sData11 = "<script src=\"http://cdn.playbuzz.com/widget/feed.js\"></script><div class=\"pb_feed\" data-embed-by=\"4302ca9d-d2cc-4bb2-a03c-1ea25faa71e1\" data-game=\"/yaelsk10/3-9-2016-8-47-02-am\" data-recommend=\"false\" data-game-info=\"false\" data-comments=\"false\" data-async=\"true\"></div> <script>if (!window.PlayBuzz) { window.addEventListener(\"PlaybuzzScriptReady\", function () { PlayBuzz.Feed.renderFeed(); }); } else { PlayBuzz.Feed.renderFeed(); }</script>";

                // parsedDocument.setText(sData11);

                parsedDocument.setAuthorName(getValue(jDocumentNode, "f7", ""));
                parsedDocument.setF16(getValue(jDocumentNode, "f16", ""));
                parsedDocument.setF22(getValue(jDocumentNode, "f22", ""));
                parsedDocument.setImageFromF2(getValue(jDocumentNode, "f2", ""));

                String sAuthorIcon = getValue(jDocumentNode, "author_icon", "");
                parsedDocument.setImageAuthor(sAuthorIcon);

                if (sAuthorIcon != "") {
                    in_authorIcon = true;
                }
                parsedDocument.setIsDeot(in_authorIcon);

                parsedDocument.setImageFromF3(getValue(jDocumentNode, "f3", ""));
                parsedDocument.setVideoArticleImageFromF9(getValue(jDocumentNode, "f9", ""));
                parsedDocument.setClipURL(getValue(jDocumentNode, "f4", ""));
                parsedDocument.setEmbeddedClip(getValue(jDocumentNode, "embedded_clip", ""));

                //Log.e("alex", "GGET VIDEO URL 1:" + jDocumentNode.toString());
                //Log.e("alex", "GGET VIDEO URL 2:" + getValue(jDocumentNode, "f3", ""));

                String sClipURL = getValue(jDocumentNode, "f19", "clip_url");
                String sClipF9ImgSrc = getValue(jDocumentNode, "f19", "clip_f9.imgsrc");
                String sAppUrl = getValue(jDocumentNode, "f19", "app_url");
                String sHtml5 = getValue(jDocumentNode, "f19", "html5");

                String sCanonicalDynasty = getValue(jDocumentNode, "canonical_dynasty", "ids");
                if (sCanonicalDynasty != "") {
                    parsedDocument.setCanonicalDynastyIds(sCanonicalDynasty);
                }

                String sCanonicalFolder = getValue(jDocumentNode, "canonical_folder", "");
                if (sCanonicalFolder != "") {
                    Log.e("alex", "setCanonicalFolder: " + sCanonicalFolder);
                    parsedDocument.setCanonicalFolder(sCanonicalFolder);

                }

                if (sClipURL != "") {
                    parsedDocument.setClipURL(sClipURL);
                    parsedDocument.setClipURLFromF19(sClipURL);
                }

                if (sClipF9ImgSrc != "") {
                    parsedDocument.setImageUrlFromF19(sClipF9ImgSrc);
                }
                if (sAppUrl != "") {
                    parsedDocument.setClipUrlHTML5(sAppUrl);
                }
                if (sHtml5 != "") {
                    parsedDocument.setClipHTML5(sHtml5);
                }

                //Log.e("alex","ParsingJsonStart_6");

                if (getValue(jDocumentNode, "tagiot", "tagit") != "") {
                    JSONArray arrTagiot = jDocumentNode.getJSONObject("tagiot").getJSONArray("tagit");

                    for (int i = 0; i < arrTagiot.length(); i++) {
                        JSONObject tagit = arrTagiot.getJSONObject(i);

                        String tagit_id = getValue(tagit, "id", "");
                        String simplified = getValue(tagit, "simplified", "");

                        if (!TextUtils.isEmpty(tagit_id) && !TextUtils.isEmpty(simplified)) {
                            Tagit oTagit = new Tagit(tagit_id, simplified, "");
                            parsedDocument.getTagiot().add(oTagit);
                        }
                    }

                    //Log.e("alex", "JSON-!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!: " + arrTagiot.length());
                }
                //Log.e("alex","ParsingJsonStart_7");
            } catch (Exception ex) {
                //Log.e("alex","JSON ERROR: " + ex);
            }
        }

        //Log.e("alex", "JSON+!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!: " + parsedDocument.getClipURL());

        Log.e("alex", "ParsingJsonEnd...");

        return this.parsedDocument;
    }

    private String getValue(JSONObject obj, String node, String att) throws JSONException {
        try {
            if (!obj.has(node)) return "";

            if (att == "") {
                if (node == "f3") {
                    //Log.e("alex","GGET VIDEO URL 0:" + obj.toString());
                    Log.e("alex", "GGET VIDEO URL 1:" + obj.getString(node));
                }
                return obj.getString(node);
            } else {
                JSONObject jNode = obj.getJSONObject(node);
                if (jNode.has(att)) {
                    return jNode.getString(att);
                }
            }
        } catch (Exception ex) {
            Log.e("alex", "ParsingJsonError: " + ex);
        }

        return "";
    }
}
