/*
Copyright (c) 2010, John Crepezzi <john@crepezzi.com>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the <organization> nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.crepezzi.tweetstream4j.types;

import net.sf.json.JSONObject;


/**
 * An object representing a deletion request from twitter.
 * from twitter:
 *      Clients are urged to honor deletion requests and discard deleted
 *      statuses immediately. At times, status deletion messages may arrive
 *      before the status. Even in this case, the late arriving status should
 *      be deleted from your backing store.
 * @author jcrepezzi
 */
public class SDeletion {

    private Long statusId, userId;
    private JSONObject json;
    
    private SDeletion() {
        //no creating your own deletions
    }

    /**
     * Parse JSON object and create/return a new SDeletion object
     * @param obj The JSON object to parse
     * @return The resultant SDeletion.
     */
    public static SDeletion parseJSON(JSONObject obj) {
        //get the proper object
        SDeletion del = new SDeletion();
        del.json = obj;
        obj = obj.getJSONObject("delete").getJSONObject("status");
        del.statusId = obj.getLong("id");
        del.userId = obj.getLong("user_id");
        return del;
    }

    public JSONObject getJSON() {
        return this.json;
    }

    /**
     * Get the status id of the deletion
     * @return The status id of the deletion
     */
    public Long getStatusId() {
        return this.statusId;
    }

    /**
     * Get the user id of the deletion
     * @return The user id of the deletion
     */
    public Long getUserId() {
        return this.userId;
    }

    @Override
    public String toString() {
        return this.statusId + " (by " + this.userId + ")";
    }

    /**
     * Equality is based on status id
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final SDeletion other = (SDeletion) obj;
        if (this.statusId != other.statusId && (this.statusId == null || !this.statusId.equals(other.statusId))) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.statusId != null ? this.statusId.hashCode() : 0);
        return hash;
    }

}
