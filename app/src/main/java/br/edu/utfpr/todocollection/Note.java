package br.edu.utfpr.todocollection;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
    private String name;
    private String content;

    public Note(String name, String content) {
        this.name = name;
        this.content = content;
    }

    private Note(Parcel source) {
        name = source.readString();
        content = source.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(content);
    }
}