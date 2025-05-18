package com.petsignal.posts.event;

import com.petsignal.alert.entity.Alert;
import com.petsignal.posts.entity.Post;

public record PostEvent(Post post, Alert alert) {
}
