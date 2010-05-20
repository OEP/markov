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
 * A class representing a user in the twitterverse.
 * @author jcrepezzi
 */
public class STweetUser {

    private Boolean profileBackgroundTile, verified, geoEnabled, notifications, isProtected, following;
    private String url, profileSidebarBorderColor, description, profileBackgroundColor,
            profileTextColor, profileImageUrl, timeZone, location, name, profileLinkColor, screenName, profileBackgroundImageUrl;
    private int followersCount, friendsCount, favouritesCount, statusesCount, utcOffset;
    private String createdAt;
    private Long userId;
    private JSONObject json;

    private STweetUser() {
        //no creating tweet users
    }

    /**
     * Parse a user from incoming JSON (from twitter streaming API)
     * @param obj The JSON object to parse
     * @return The resultant STweetUser
     */
    static STweetUser parseJSON(JSONObject obj) {
        STweetUser user = new STweetUser();
        user.json = obj;
        user.userId = obj.getLong("id");
        user.screenName = obj.getString("screen_name");
        user.profileBackgroundTile = obj.getBoolean("profile_background_tile");
        user.verified = obj.getBoolean("verified");
        user.geoEnabled = obj.getBoolean("geo_enabled");
        user.notifications = obj.optBoolean("notifications");
        user.isProtected = obj.getBoolean("protected");
        user.following = obj.optBoolean("following");
        user.url = obj.getString("url");
        user.profileSidebarBorderColor = obj.getString("profile_sidebar_border_color");
        user.description = obj.getString("description");
        user.profileBackgroundColor = obj.getString("profile_background_color");
        user.profileTextColor = obj.getString("profile_text_color");
        user.profileImageUrl = obj.getString("profile_image_url");
        user.timeZone = obj.getString("time_zone");
        user.location = obj.getString("location");
        user.name = obj.getString("name");
        user.profileLinkColor = obj.getString("profile_link_color");
        user.profileBackgroundImageUrl = obj.getString("profile_background_image_url");
        user.followersCount = obj.getInt("followers_count");
        user.friendsCount = obj.getInt("friends_count");
        user.favouritesCount = obj.getInt("favourites_count");
        user.statusesCount = obj.getInt("statuses_count");
        user.createdAt = obj.getString("created_at");
        user.utcOffset = obj.optInt("utc_offset");
        return user;
    }

    public JSONObject getJSON() {
        return this.json;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getDescription() {
        return description;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public Boolean getFollowing() {
        return following;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public Boolean getGeoEnabled() {
        return geoEnabled;
    }

    public Boolean getIsProtected() {
        return isProtected;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public Boolean getNotification() {
        return notifications;
    }

    public String getProfileBackgroundColor() {
        return profileBackgroundColor;
    }

    public String getProfileBackgroundImageUrl() {
        return profileBackgroundImageUrl;
    }

    public Boolean getProfileBackgroundTile() {
        return profileBackgroundTile;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getProfileLinkColor() {
        return profileLinkColor;
    }

    public String getProfileSidebarBorderColor() {
        return profileSidebarBorderColor;
    }

    public String getProfileTextColor() {
        return profileTextColor;
    }

    public String getScreenName() {
        return screenName;
    }

    public int getStatusesCount() {
        return statusesCount;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getUrl() {
        return url;
    }

    public Long getUserId() {
        return userId;
    }

    public int getUtcOffset() {
        return utcOffset;
    }

    public Boolean getVerified() {
        return verified;
    }

}
