package ch.eltra.notkauf;

class DetailsItem {
    private String mText1;
    private String mText2;

    DetailsItem(String text1, String text2) {
        mText1 = text1;
        mText2 = text2;
    }

    String getText1() {
        return mText1;
    }

    String getText2() {
        return mText2;
    }
}
