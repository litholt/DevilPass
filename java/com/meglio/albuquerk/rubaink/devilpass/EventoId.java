package com.meglio.albuquerk.rubaink.devilpass;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class EventoId {
    @Exclude
    String EventoId;

    public <T extends EventoId> T withId(@NonNull final String id) {
        this.EventoId = id;
        return (T) this;
    }
}
