package ch.eltra.notkauf;

public class RecyclerItem {
    private String mText1;
    private String mText2;
    private String mText3;
    private Boolean mFood;
    private Boolean mDrugs;
    private Boolean mCar;
    private Boolean mOther;
    private int mPeople;

    public RecyclerItem(String text1, String text2, String text3, Boolean food, Boolean drugs, Boolean car, Boolean other, int people) {
        mText1 = text1;
        mText2 = text2;
        mText3 = text3;
        mFood = food;
        mDrugs = drugs;
        mCar = car;
        mOther = other;
        mPeople = people;
    }

    public String getText1() {
        return mText1;
    }

    public String getText2() {
        return mText2;
    }

    public String getText3() { return mText3; }

    public Boolean getFood() { return mFood; }

    public Boolean getDrugs() { return mDrugs; }

    public Boolean getCar() { return mCar; }

    public Boolean getOther() { return mOther; }

    public int getPeople() { return mPeople; }
}
