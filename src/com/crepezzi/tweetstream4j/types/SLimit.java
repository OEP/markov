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
 * An object representing a limit from twitter
 * from twitter:
 *      Track streams may also contain limitation notices, where the integer
 *      track is an enumeration of statuses that matched the track predicate
 *      but were administratively limited. These notices will be sent each time
 *      a limited stream becomes unlimited.
 * @author based
 */
public class SLimit {

    private Long track;
    private JSONObject json;

    private SLimit() {
        //no creating limits
    }

    /**
     * Create a SLimit object from a JSONObject
     * @param obj The JSONObject to parse
     * @return The resultant SLimit
     */
    public static SLimit parseJSON(JSONObject obj) {
        SLimit lim = new SLimit();
        lim.json = obj;
        lim.track = obj.getJSONObject("limit").getLong("track");
        return lim;
    }

    public JSONObject getJSON() {
        return this.json;
    }
    
    public Long getTrack() {
        return this.track;
    }

    @Override
    public String toString() {
        if (track == null) return null;
        return track.toString();
    }

    /**
     * Equality is based on status id
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final SLimit other = (SLimit) obj;
        if (this.track != other.track && (this.track == null || !this.track.equals(other.track))) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.track != null ? this.track.hashCode() : 0);
        return hash;
    }

}
