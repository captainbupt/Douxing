package com.badou.mworking.net.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

/**
 * Created by yee on 2/28/14.
 */
public class ResponseError extends VolleyError {

    public ResponseError() {
    }

    public ResponseError(NetworkResponse response) {
        super(response);
    }

    public ResponseError(String exceptionMessage) {
        super(exceptionMessage);
    }

    public ResponseError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
    }

    public ResponseError(Throwable cause) {
        super(cause);
    }
}
