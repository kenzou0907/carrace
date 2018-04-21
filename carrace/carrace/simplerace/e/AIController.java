package simplerace.e;
import simplerace.*;

/**
 * AIController
 * now : 単純に次の旗との距離が近い方に向かわせる
 * Todo : 判定に自身の角度も使いたい
 * 先着、次着の判定もさせたい、先着なら回り込むように旗を取らせたい
 */

public class AIController implements Controller, Constants {

    /**
     * 自身のセンサ情報が束縛されるフィールド.
     */
    private SensorModel inputs;

    /**
     * 狙うべき旗の位置と自身の角度が代入されるフィールド.
     */
    private double targetAngle;

    /**
     * こいつが狙うべき旗の位置が束縛されるフィールド.
     */
    private Vector2d targetFlag;

    /**
     * バックでbackwardleftとかを押しっぱなしにして収束する旋回速度
     * ここを最低速度としてあらゆる行動を行う
     */
    private final double lowestTurnSpeed = -2.564335;

    /**
     * 当たり判定の距離を対角線で割った値
     * getDistanceシリーズは対角線で割った値が求められるので作った
     */
    private final double collideDetection = 20.0/Math.sqrt(320000.0D);

    /**
     * 統計情報を取る
     */
    private Analyst analyst;

    public void reset(){
        this.analyst = new Analyst();
    }

    /**
     * 色々更新する
     */
    private void update(SensorModel inputs){
        this.inputs = inputs;
        DataCenter.getSingleton().update(this);
        this.targetFlag = DataCenter.getSingleton().operation(this);
        this.targetAngle = Calculator.getAngleBetweenCarAndWaypoint(this, this.targetFlag);
    }

    /**
     * isAbleToBrake 今からブレーキして目標速度まで落とすと止まり切れるか返す
     *
     * 　　n      ♪キボウノハナー
     *　 _H
     *　巛 ｸ　 ノﾚzz　　　　　　　　俺は止まんねえからよ...
     *　 F｜　幺 ﾐwｯﾐ
     *　｜｜　ヽﾚvvｲ             お前らが止まんねえ限り、
     *　｜ ￣⌒＼二ヽ＿
     *　 ￣￣Ｙ　ミ　 /|       俺はその先にいるぞ！！！！！
     *　　　 ｜　 |　｜|
     *　　　 /　　|　｜|
     *　　　/　　 |　 L|           だからよ...
     *　　　＼＿_/|＿/(ヽ
     *　　　 ｜　　 ｜/ぐ)
     *　　　 ｜　 ﾊ ∧＼≫        止まるんじゃねえぞ...
     *　　　 ｜　/ Ｖ∧
     *　　　 ｜ ｜　Ｖ｜
     * @return true 止まり切れる : false 止まり切れない(ブレーキしなさい)
     */
    private boolean isAbleToBrake(){
        double finPoint = (Calculator.getDistanceBetweenCarAndWaypoint(this, this.targetFlag) - this.collideDetection) * Math.sqrt(320000.0D);
        double speed = this.inputs.getSpeed();
        double targetSpeed = 0;
        if(Calculator.areTheyEqual(this.targetFlag, this.inputs.getNextWaypointPosition()))
            targetSpeed = this.lowestTurnSpeed;
        
        double distance = 0;
        if(targetSpeed < speed) return true;

        while(true){
            if(speed >= targetSpeed) break;
            distance -= speed;
            speed += 0.425;
        }

        if(distance > finPoint) return false;

        return true;
    }

    /**
     * ControllerをImplementsすると必要になる
     * 実際に操縦を行うメソッド
     *
     * @param inputs センサ情報
     * @return 操縦コマンド
     */
    public int control (SensorModel inputs) {

        //this.turnStartProcess(inputs);

        int command = neutral;

        this.update(inputs);

        if(this.targetAngle > 0){
            command = backwardleft;

            if(this.targetAngle > 3.0) command = backward;

            if(!this.isAbleToBrake()) command = forwardleft;
        }else{
            command = backwardright;

            if(this.targetAngle < -3.0) command = backward;

            if(!this.isAbleToBrake()) command = forwardright;
        }

        //this.turnEndProcess();

        return command;
    }

    /*-------------------------Analyze------------------------------*/

    /**
     * ターン開始時に行う操作
     * @author takaesumizuki
     */
    private void turnStartProcess(SensorModel inputs) {
        this.analyst.update(inputs); /* 統計情報をとる */

        return;
    }

    /**
     * ターン終了時に行う操作
     * @author takaesumizuki
     */
    private void turnEndProcess() {
        // this.analyst.printResult(); /* ターンごとの結果を表示したくないならコメントアウトしてください */
        if (this.analyst.isLastTurn()) {
            // this.analyst.printResult(); /* ゲームごと(ラウンドごと)の結果を表示したくないならコメントアウトしてください */
            if (this.analyst.isFinalRound()) {
                this.analyst.finalRoundProcess();/* 最終結果を表示したくないならコメントアウトしてください*/
            }
        }

        return;
    }

    /*---------------------------getter-----------------------------*/

    /**
     * 自身のセンサ情報を返す.
     * @return
     */
    public SensorModel getSensor() {
        return this.inputs;
    }

    /**
     * 自身から1つめのWaypointへの角度を返す.
     * @return
     */
    protected double getTargetAngle(){
        return this.targetAngle;
    }

    /*--------------------------------------------------------------*/

    /**
     * 自身の文字列を応答する
     * @return 自身の文字列
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("AIController");
        sb.append(this.hashCode());
        return sb.toString();
    }

    /**
     * 自身を表すハッシュ値を応答する
     * @return
     */
    @Override
    public int hashCode(){

        return super.hashCode();
    }
}

