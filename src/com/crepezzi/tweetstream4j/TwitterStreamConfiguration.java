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

package com.crepezzi.tweetstream4j;

import com.crepezzi.tweetstream4j.ext.Base64Coder;

/**
 * A configuration class for describing to TweetRiver how to set up a TwitterStream.
 * @author jcrepezzi
 */
public class TwitterStreamConfiguration {

    private String b64;
    private Integer count = null, delimitedLength = null;

    /**
     * Username and password are required
     * @param username The username to use
     * @param password The password to use
     */
    public TwitterStreamConfiguration(String username, String password) {
        this.setCredentials(username, password);
    }

    /**
     * Get a base 64 encoding used for basic HTTP Auth.
     * @return Base 64 encoding (String)
     */
    protected String getb64() {
        return this.b64;
    }

    /**
     * Set the base 64 string from the username (don't actually store the username/pass)
     * @param username
     * @param password
     */
    public void setCredentials(String username, String password) {
        this.b64 = Base64Coder.encodeString(username + ":" + password);
    }

    /**
     * get the count associated with this configuration object.
     * @return The count
     * @see setCount
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Set the count for all requests issued with this configuration object.
     * from twitter:
     *      Indicates the number of previous statuses to consider for delivery
     *      before transitioning to live stream delivery. On unfiltered streams,
     *      all considered statuses are delivered, so the number requested is
     *      the number returned. On filtered streams, the number requested is
     *      the number of statuses that are applied to the filter predicate,
     *      and not the number of statuses returned.
     * @param count The count for all calls with this twitter configuration object
     * @see getCount
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * Get the delimited length associated with this configuration object.
     * @return The delimited length
     * @see setDelimitedLength
     */
    public Integer getDelimitedLength() {
        return delimitedLength;
    }

    /**
     * Set the delimited length for all requests issued with this configuration
     * object.
     * from twitter:
     *      Indicates that statuses should be delimited in the stream. Statuses
     *      are represented by a length, in bytes, a newline, and the status
     *      text that is exactly length bytes. Note that "keep-alive" newlines
     *      may be inserted before each length.
     * @param delimited_length The delimited length to set
     */
    public void setDelimitedLength(Integer delimited_length) {
        this.delimitedLength = delimited_length;
    }

}
