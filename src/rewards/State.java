package rewards;

public class State {
    int Xcoordinate;
    int Ycoordinate;
    int blockStatus;
    public State(int X, int Y, int status){
        Xcoordinate = X;
        Ycoordinate = Y;
        blockStatus = status;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof State)) {
            return false;
        }

        State state = (State) o;

        return state.Xcoordinate == Xcoordinate &&
                state.Ycoordinate == Ycoordinate && state.blockStatus == blockStatus;
    }
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Xcoordinate;
        result = 31 * result + Ycoordinate;
        result = 31*result + blockStatus;
        return result;
    }
}
