package com.axway.apigw.android;

import android.content.DialogInterface;
import android.os.Bundle;

public class Constants {

	public static final long NULL_ID = -1;
	
	public static final String UTF8 = "UTF-8";
	
	public static final String DEF_API_USERNAME = "admin";
	public static final String DEF_API_PASSWD = "changeme";
	
    public static final String TOPOLOGY_FILE_EXT = ".topo";
    public static final String SAMPLE_FILE = "sample" + TOPOLOGY_FILE_EXT;
    
    public static final int STATUS_INACTIVE = 0;
    public static final int STATUS_ACTIVE = 1;

    public static final int FLAG_CERT_NOT_TRUSTED = 0;
    public static final int FLAG_CERT_TRUSTED = 1;

	public static final String HTTP_SCHEME = "http";
	public static final String HTTPS_SCHEME = HTTP_SCHEME + "s";

	public static final String EXTRA_BASE = Constants.class.getPackage().getName() + "."; // "com.axway.agrca.";
	public static final String EXTRA_ACTION = EXTRA_BASE + "action.id";
    public static final String EXTRA_INTENT_ACTION = EXTRA_BASE + "intent.action";
	public static final String EXTRA_TRUST_CERT = EXTRA_BASE + "trust_cert";
	
	public static final String EXTRA_JSON_ITEM = EXTRA_BASE + "json.item";
	public static final String EXTRA_ITEM_ID = EXTRA_BASE + "item.id";
	public static final String EXTRA_ITEM_NAME = EXTRA_BASE + "item.name";
	public static final String EXTRA_ITEM_TYPE = EXTRA_BASE + "item.type";
	public static final String EXTRA_JSON_TOPOLOGY = EXTRA_BASE + "json.topology";
    public static final String EXTRA_JSON_GROUP = EXTRA_BASE + "json.group";
    public static final String EXTRA_KPS_STORE_ID = EXTRA_BASE + "kps.store.id";
    public static final String EXTRA_KPS_STORE_ALIAS = EXTRA_BASE + "kps.store.alias";
    public static final String EXTRA_KPS_TYPE_ID = EXTRA_BASE + "kps.type.id";
    public static final String EXTRA_INSTANCE_ID = EXTRA_BASE + "instance.id";
	public static final String EXTRA_DELETE_FROM_DISK = EXTRA_BASE + "del.from.disk";
	public static final String EXTRA_SERVICES_PORT = EXTRA_BASE + "svcs.port";
	public static final String EXTRA_MGMT_PORT = EXTRA_BASE + "mgmt.port";
	public static final String EXTRA_REFERRING_ITEM_TYPE = EXTRA_BASE + "ref.item.type";
	public static final String EXTRA_REFERRING_ITEM_ID = EXTRA_BASE + "ref.id";
	public static final String EXTRA_URL = EXTRA_BASE + "url";
	public static final String EXTRA_SERVER_INFO = EXTRA_BASE + "srvr.info";
	public static final String EXTRA_USE_SSL = EXTRA_BASE + "use.ssl";
	public static final String EXTRA_NODE_MGR_GROUP = EXTRA_BASE + "nm.grp";
	public static final String EXTRA_CONSOLE_HANDLE = EXTRA_BASE + "console.handle";
	public static final String EXTRA_FROM_GROUP = EXTRA_BASE + "from.grp";
	public static final String EXTRA_TO_GROUP = EXTRA_BASE + "to.grp";
	public static final String EXTRA_LAYOUT_ID = EXTRA_BASE + "layout.id";
	public static final String EXTRA_COMPARE_RESULT = EXTRA_BASE + "compare.result";
	public static final String EXTRA_HAVE_CONSOLE = EXTRA_BASE + "have.console";
	public static final String EXTRA_HOST_ID = EXTRA_BASE + "host.id";
    public static final String EXTRA_GROUP_ID = EXTRA_BASE + "grp.id";
	public static final String EXTRA_FILENAME = EXTRA_BASE + "filename";
    public static final String EXTRA_ERROR_MSG = EXTRA_BASE + "err.msg";
    public static final String EXTRA_ERROR_CODE = EXTRA_BASE + "err.code";
    public static final String EXTRA_MULTI_PANE = EXTRA_BASE + "multi.pane";
    public static final String EXTRA_HOST_NAMES = EXTRA_BASE + "host.names";
    public static final String EXTRA_GROUP_NAMES = EXTRA_BASE + "grp.names";
    public static final String EXTRA_GATEWAY_STATUS = EXTRA_BASE + "gtw.status";
    public static final String EXTRA_SELECTED_POS = EXTRA_BASE + "sel.pos";
    public static final String EXTRA_PARSE_RESPONSE = EXTRA_BASE + "parse.resp";
    public static final String EXTRA_EXTRAS = EXTRA_BASE + "extras";
    public static final String EXTRA_ADD_NEW_GROUP = EXTRA_BASE + "add.grp";
    public static final String EXTRA_IS_DIRTY = EXTRA_BASE + "is.dirty";
    public static final String EXTRA_CUR_FRAG_TAG = EXTRA_BASE + "frag.tag";
    public static final String EXTRA_CUR_FRAG_ARGS = EXTRA_BASE + "frag.args";
    public static final String EXTRA_DOMAIN_PHRASE = EXTRA_BASE + "domain.pp";
    public static final String EXTRA_TEMP_PHRASE = EXTRA_BASE + "temp.pp";
    public static final String EXTRA_SIGN_ALG = EXTRA_BASE + "sign.alg";
    public static final String EXTRA_CA_OPTION = EXTRA_BASE + "ca.opt";
    public static final String EXTRA_HOST_NAME = EXTRA_BASE + "host.nm";
    public static final String EXTRA_GROUP_NAME = EXTRA_BASE + "grp.nm";
    public static final String EXTRA_CUR_PAGE = EXTRA_BASE + "cur.pg";

	public static final String KEY_REMEMBER_USER = "rememberUser";
	public static final String KEY_SSHUSER = "sshUser";
    public static final String KEY_RESULT = "result";
    public static final String KEY_ERRORS = "errors";
    public static final String KEY_SHOW_INTERNAL_KPS = EXTRA_BASE + "showInternalKps";
    public static final String KEY_GOOGLE_ACCT = EXTRA_BASE + "googleAcct";
    public static final String KEY_BATCH_SIZE = EXTRA_BASE + "batchSize";
    public static final String KEY_KPS_DISPLAY_PREF = EXTRA_BASE + "kpsDisplayPref";
    public static final String KEY_REGISTRATION_ID = "gcmRegistrationId";
    public static final String KEY_GCM_REGISTERED = EXTRA_BASE + "gcmRegistered";
    public static final String KEY_APP_VERSION = EXTRA_BASE + "appVersion";
    public static final String KEY_LAST_NAV_ID = EXTRA_BASE + "lastNavId";
    public static final String KEY_SHOW_ADVISORIES = EXTRA_BASE + "showAdvisoryTopics";
    public static final String KEY_WIFI_ONLY = EXTRA_BASE + "wifiOnly";
    public static final String KEY_KPS_FOLDER = EXTRA_BASE + "kpsFolder";
    public static final String KEY_DEPLOY_FOLDER = EXTRA_BASE + "deployFolder";
    public static final boolean DEF_SHOW_NODE_MGRS = false;
    public static final boolean DEF_SHOW_INTERNAL_KPS = false;

    public static final int DEF_BATCH_SIZE = 50;

	public static final int FLAG_SHOW_NODE_MGRS   = 0x0001;
	public static final int FLAG_RELOAD_AFTER_UPD = 0x0010;
			
	public static final int CERT_NOT_TRUSTED = 1001;
	public static final int CERT_TRUSTED = 1002;
	public static final int TRUST_STORE_REMOVED = 1003;
    public static final int CERT_TRUST_ERROR = 1004;

	public static final String JACKPAL_TERMINAL_PACKAGE = "jackpal.androidterm";
	public static final String JACKPAL_EXTRA_WINDOW_HANDLE = "jackpal.androidterm.window_handle";
	public static final String JACKPAL_EXTRA_INITIAL_CMD = "jackpal.androidterm.iInitialCommand";
	public static final String JACKPAL_ACTION_RUN_SCRIPT = "jackpal.androidterm.RUN_SCRIPT";
	public static final String JACKPAL_ACTION_NEW_WINDOW = "jackpal.androidterm.OPEN_NEW_WINDOW";
	
	public static final int DEF_HEALTHCHECK_SECS = 60;

    public static final Bundle EMPTY_BUNDLE = new Bundle();

    public static final DialogInterface.OnClickListener NOOP_LISTENER = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
        }
    };
    public static final String TAG_PROGRESS = "progFrag";
    public static final String TAG_SINGLE_PANE = "singlePane";

    public static final String ACCOUNT_TYPE = "com.axway.agrca";
    public static final String AUTH_TOKEN_TYPE = ACCOUNT_TYPE + ".auth";

    public static final String GCM_PROJECT_ID = "659961194860"; //"404585072562";
    public static final String GCM_API_KEY = "AIzaSyDk2N-aVBDxJnPJ4NxlP6zKxjW8zky23MM"; //"AIzaSyBBrkVTbUJO5frtejtYP04p2Y1PGN4vvcc"; //"AIzaSyDnadiiUUf05DtCkvw0LjUDJEYpXyu5hJU";
    public static final String EXTRA_ATTR_NAME = "attr_name";

    public static final int MSG_BASE = 100;
    public static final int MSG_REFRESH = MSG_BASE + 1;
    public static final int MSG_SELECT = MSG_BASE + 2;
    public static final int MSG_ACTION = MSG_BASE + 3;
}
