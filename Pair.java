public class Pair<A,B> {
    private A alpha;
    private B beta;

    public Pair(A alpha, B beta) {
        this.alpha = alpha;
        this.beta = beta;
    }

    public A getAlpha() {
        return alpha;
    }

    public B getBeta() {
        return beta;
    }

    public void setAlpha(A alpha) {
        this.alpha = alpha;
    }

    public void setBeta(B beta) {
        this.beta = beta;
    }
}
