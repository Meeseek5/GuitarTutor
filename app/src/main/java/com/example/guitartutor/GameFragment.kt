package com.example.guitartutor

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate


/**
 * A simple [Fragment] subclass.
 * Use the [GameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    // 紀錄切換次數
    var count = 0

    // 亂數和弦的index
    var randomChordListIndex = 0

    // 使用者本回合花費時間(seconds)
    var totalSecond = ""

    // 最短花費時間紀錄
    var shortestSecond = ""
    var totalSecondInt = 0
    var shortestSecondInt = 0

    // 和弦累積到足夠量才觸發切換
    var a_chord_accu = 0
    var c_chord_accu = 0
    var d_chord_accu = 0
    var e_chord_accu = 0
    var g_chord_accu = 0
    var chordList = arrayOf("A", "C", "D", "E", "G")
    lateinit var randomChordList: Array<String?>

    // 紀錄亂數顯示的和弦
    var currentChord: String? = ""
    var countdowntimer: CountDownTimer? = null
    var textSwitcher: TextSwitcher? = null
    var buttonStart: Button? = null
    var buttonRestart: Button? = null

    // 提示當前和弦的圖，按下後用Toast顯示
    var textViewChordImage: TextView? = null

    // 秒數TextView
    var textViewShowSecond: TextView? = null

    // 提示TextView:Ready
    var textViewHint: TextView? = null

    // 顯示偵測到的和絃TextView
    var textViewShowChord: TextView? = null

    // 顯示偵測到和弦的Hz
    var textViewPitch: TextView? = null

    // 本回合花費時間
    var textViewTotalSecond: TextView? = null

    // 最短花費時間
    var textViewShortestSecond: TextView? = null

    // 說明文字
    var cardView: CardView? = null

    // 宣告AudioDispatcher
    private var dispatcher: AudioDispatcher? = null

    // ProgressBar視覺化倒數
    private var progressBar: ProgressBar? = null

    // Toast 顯示和弦
    var show: TextView? = null

    // 在和弦圖片陣列的索引
    var drawableIndex = 0

    // 日期物件
    var date = Date()
    var dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")

    // SQLite使用的變數
    private val DB_NAME = "record.db"
    private val TABLE_NAME = "record"
    private val DB_VERSION = 1
    var mDBHelper: SQLiteDataBaseHelper? = null

    // 定義model
    var modelPath = "soundclassifier_with_metadata.tflite"
    // defining the minimum threshold
    var probabilityThreshold: Float = 0.3f

    var globelOuputStr2 = "No input"

    // UI thread的Handler
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                TEXT_TO_SWITCH -> next(null)
                INVISIBLE_AND_VISIBLE_SOMETHING -> {
                    // TextSwitcher、ProgressBar、TextSecond 隱藏
                    textSwitcher!!.visibility = View.INVISIBLE
                    progressBar!!.visibility = View.INVISIBLE
                    textViewShowSecond!!.visibility = View.INVISIBLE
                    textViewChordImage!!.visibility = View.INVISIBLE
                    textViewShowChord!!.visibility = View.INVISIBLE
                    textViewPitch!!.visibility = View.INVISIBLE

                    // Restart按鈕、本次秒數、最短秒數
                    buttonRestart!!.visibility = View.VISIBLE
                    textViewTotalSecond!!.visibility = View.VISIBLE
                    textViewShortestSecond!!.visibility = View.VISIBLE
                }
                START_COUNT_DOWN_TIMING -> startTiming()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 原始fragment的判斷式
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }

        // 檢查Permission是否取得
        checkPermission(Manifest.permission.RECORD_AUDIO, AUDIO_PERMISSION_CODE)

        // 初始化資料庫
        mDBHelper = SQLiteDataBaseHelper(context, DB_NAME, null, DB_VERSION, TABLE_NAME)

        // 查看SQLite裡面有沒有今天紀錄
        if (mDBHelper!!.getSecondFromDate(dateFormat.format(date)) == "" ||
                mDBHelper!!.getSecondFromDate(dateFormat.format(date)) == null) {
            shortestSecondInt = 120
        } else {
            shortestSecondInt = Integer.parseInt(mDBHelper!!
                    .getSecondFromDate(dateFormat.format(date)))//.toInt()
        }

        dispatcher = null


        /* model 設定*/
        // Loading model
        val classifier = AudioClassifier.createFromFile(context, modelPath) // p1:this

        // TODO 3.1: Creating an audio recorder
        val tensor = classifier.createInputTensorAudio()

        // TODO 3.3: Creating
        val record = classifier.createAudioRecord()
        record.startRecording()


        Timer().scheduleAtFixedRate(1, 500) {

            // TODO 4.1: Classifing audio data
            val numberOfSamples = tensor.load(record)
            val output = classifier.classify(tensor)

            // TODO 4.2: Filtering out classifications with low probability
            val filteredModelOutput = output[0].categories.filter {
                it.score > probabilityThreshold
            }

            // TODO 4.3: Creating a multiline string with the filtered results
            val outputStr =
                    filteredModelOutput.sortedBy { -it.score }
                            .joinToString(separator = "\n") { "${it.label}" }


            if(outputStr.isNotEmpty()) {
                globelOuputStr2 = outputStr.substring(1,3)
            }

            
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 取的layout上的元件
        // Fragment要加 getView()
        textSwitcher = requireView().findViewById<View>(R.id.text_switcher) as TextSwitcher
        buttonStart = requireView().findViewById<View>(R.id.image_button_start) as Button
        buttonRestart = requireView().findViewById<View>(R.id.image_button_retart) as Button

        // 計時秒數
        textViewShowSecond = requireView().findViewById<View>(R.id.text_view_show_second) as TextView
        // 顯示開頭文字
        textViewHint = requireView().findViewById<View>(R.id.text_view_show_ready) as TextView
        // textViewHint.setText("Ready ?");

        // 類似Button的TextView，按下可顯和弦圖Toast
        textViewChordImage = requireView().findViewById<View>(R.id.show) as TextView


        // 暫時顯示偵測到和弦，考慮是否要拿掉
        textViewShowChord = requireView().findViewById<View>(R.id.text_view_chord) as TextView
        // 暫時顯示和弦freq，考慮是否要拿掉
        textViewPitch = requireView().findViewById<View>(R.id.text_view_pitch) as TextView

        // 最短花費秒數
        textViewShortestSecond = requireView().findViewById<View>(R.id.text_view_best_second) as TextView
        // 本次花費秒數
        textViewTotalSecond = requireView().findViewById<View>(R.id.text_view_total_second) as TextView

        // 說明文字
        cardView = requireView().findViewById<View>(R.id.cardview) as CardView
        cardView!!.setContentPadding(20, 20, 20, 20)

        // 和弦提示圖示
        show = requireView().findViewById<View>(R.id.show) as TextView
        val chord_resource = intArrayOf(R.drawable.a_chord_white, R.drawable.c_chord_white,
                R.drawable.d_chord_white, R.drawable.e_chord_white, R.drawable.g_chord_white)


        // TextSwitcher初始化
        textSwitcher!!.setFactory {
            val textView = TextView(context)
            textView.textSize = 125f
            textView.setTextColor(Color.parseColor("#888888"))
            textView.gravity = Gravity.CENTER_HORIZONTAL
            textView
        }

        // 初始畫面restart button、score textview、progressBar先隱藏
        buttonRestart!!.visibility = View.INVISIBLE
        textViewChordImage!!.visibility = View.INVISIBLE
        textViewShowChord!!.visibility = View.INVISIBLE
        textViewPitch!!.visibility = View.INVISIBLE


        // on click on show text images toast will be shown
        show!!.setOnClickListener { // Initialising Toast
            /** 原始Toast建構子參數為getApplicationContext()  */
            /** 原始Toast建構子參數為getApplicationContext()  */
            val toast = Toast(context)
            val view = ImageView(context)
            when (currentChord) {
                "A" -> drawableIndex = 0
                "C" -> drawableIndex = 1
                "D" -> drawableIndex = 2
                "E" -> drawableIndex = 3
                "G" -> drawableIndex = 4
            }

            // set image resource to be shown
            view.setImageResource(chord_resource[drawableIndex])
            // 調整Toast位置
            toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
            // setting view to toast
            toast.setView(view)
            // showing toast
            toast.show()
        }


        // start button監聽器
        buttonStart!!.setOnClickListener { /* 初始化Dispatcher */
            // initDispatcher()
            initCheckChord()
            textSwitcher!!.visibility = View.VISIBLE
            progressBar!!.visibility = View.VISIBLE
            textViewChordImage!!.visibility = View.VISIBLE
            textViewShowChord!!.visibility = View.VISIBLE
            // textViewPitch!!.visibility = View.VISIBLE
            buttonStart!!.visibility = View.INVISIBLE
            textViewHint!!.visibility = View.INVISIBLE
            cardView!!.visibility = View.INVISIBLE
            a_chord_accu = 0
            c_chord_accu = 0
            d_chord_accu = 0
            e_chord_accu = 0
            g_chord_accu = 0
            count = 0
            randomChordListIndex = 0
            handler.sendEmptyMessage(TEXT_TO_SWITCH)
            handler.sendEmptyMessage(START_COUNT_DOWN_TIMING)
            randomChordList = arrayOfNulls(chordList.size)
            val tmpRandomNumber = createRandomNumberList()


            for (i in randomChordList.indices) {
                randomChordList[i] = chordList[tmpRandomNumber[i]]
            }

        }

        // restart button監聽器
        buttonRestart!!.setOnClickListener {
            textSwitcher!!.visibility = View.VISIBLE
            progressBar!!.visibility = View.VISIBLE
            textViewShowSecond!!.visibility = View.VISIBLE
            textViewChordImage!!.visibility = View.VISIBLE
            textViewShowChord!!.visibility = View.VISIBLE
            // textViewPitch!!.visibility = View.VISIBLE
            buttonRestart!!.visibility = View.INVISIBLE
            textViewHint!!.visibility = View.INVISIBLE
            textViewTotalSecond!!.visibility = View.INVISIBLE
            textViewShortestSecond!!.visibility = View.INVISIBLE
            a_chord_accu = 0
            c_chord_accu = 0
            d_chord_accu = 0
            e_chord_accu = 0
            g_chord_accu = 0
            count = 0
            randomChordListIndex = 0
            handler.sendEmptyMessage(TEXT_TO_SWITCH)
            // 叫UI thread啟動計時器
            handler.sendEmptyMessage(START_COUNT_DOWN_TIMING)
            randomChordList = arrayOfNulls(chordList.size)
            val tmpRandomNumber = createRandomNumberList()
            for (i in randomChordList.indices) {
                randomChordList[i] = chordList[tmpRandomNumber[i]]
            }
        }

        // ProgressBar進度顯示
        progressBar = requireActivity().findViewById(R.id.progressBar)
        val msFuture: Long = 120
        progressBar?.setMax(msFuture.toInt())
        // 創建好先不顯示，等按下開始鈕才顯示
        progressBar?.setVisibility(View.INVISIBLE)
    }

    override fun onPause() {
        super.onPause()
        // 讓dispatcher釋放資源
        if (dispatcher != null) {
            dispatcher!!.stop()
        }
    }

    /**
     * 檢查Permission
     */
    fun checkPermission(permission: String, requestCode: Int) {
        /** MainActivity -> getActivity() */
        if (ContextCompat.checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
        } else {
            // Toast.makeText(MainActivity.this, "Record Audio Permission already granted", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Record Audio Permission already granted")
            // 取得權限要做的事...
            // 初始化Dispatcher
            // initDispatcher();
        }
    }

    /**
     * 檢查彈奏的和弦
     */
    fun checkChord() {
        // 跑完五個和弦都顯示後結束
        if (checkCount() == 5) {
            handler.sendEmptyMessage(INVISIBLE_AND_VISIBLE_SOMETHING)
            // 在畫面顯示本次花費秒數
            textViewTotalSecond!!.text = totalSecond
            // 如果本次秒數比之前少，則更新最短秒數
            if (totalSecondInt < shortestSecondInt) {
                shortestSecondInt = totalSecondInt

                shortestSecond = java.lang.String.format("本日最短秒數 : %ss\n", shortestSecondInt)
                textViewShortestSecond!!.text = shortestSecond

                // 更新今日的最短時間紀錄
                if (mDBHelper!!.checkDate(dateFormat.format(date))) {
                    mDBHelper!!.updateData(dateFormat.format(date), Integer.toString(shortestSecondInt))
                    Toast.makeText(context, "update data", Toast.LENGTH_SHORT).show()
                } else {
                    mDBHelper!!.addData(dateFormat.format(date), Integer.toString(shortestSecondInt))
                    Toast.makeText(context, "add new data", Toast.LENGTH_SHORT).show()
                }
            } else {
                shortestSecond = java.lang.String.format("本日最短秒數 : %ss\n", shortestSecondInt)
                textViewShortestSecond!!.text = shortestSecond
            }
            countdowntimer!!.cancel()
        }

        textViewShowChord!!.text = globelOuputStr2

        if (globelOuputStr2 == "A") {
            handler.sendEmptyMessage(TEXT_TO_SWITCH)
            count++
        } else if (globelOuputStr2 == "C") {
            handler.sendEmptyMessage(TEXT_TO_SWITCH)
            count++
        } else if (globelOuputStr2 == "D") {
            handler.sendEmptyMessage(TEXT_TO_SWITCH)
            count++
        } else if (globelOuputStr2 == "E") {
            handler.sendEmptyMessage(TEXT_TO_SWITCH)
            count++
        } else if (globelOuputStr2 == "G") {
            handler.sendEmptyMessage(TEXT_TO_SWITCH)
            count++
        }

    }


    /**
     * 顯示偵測到的和弦
     */
//    fun processPitch(pitchInHz: Float) {
//        textViewPitch!!.text = pitchInHz.toString()
//        // 跑完五個和弦都顯示後結束
//        if (checkCount() == 5) {
//            handler.sendEmptyMessage(INVISIBLE_AND_VISIBLE_SOMETHING)
//            // 在畫面顯示本次花費秒數
//            textViewTotalSecond!!.text = totalSecond
//            // 如果本次秒數比之前少，則更新最短秒數
//            if (totalSecondInt < shortestSecondInt) {
//                shortestSecondInt = totalSecondInt
//
//                shortestSecond = java.lang.String.format("本日最短秒數 : %ss\n", shortestSecondInt)
//                textViewShortestSecond!!.text = shortestSecond
//
//                /* 使用Shared Preferences分享資料給歷史紀錄fragment
//                SharedPreferences recordShortestSecond = getContext().getSharedPreferences("record_shortest_second",MODE_PRIVATE);
//                recordShortestSecond.edit()
//                        .putInt("SHORTEST_SECOND",shortestSecondInt)
//                        .commit();
//                */
//
//                // 更新今日的最短時間紀錄
//                if (mDBHelper!!.checkDate(dateFormat.format(date))) {
//                    mDBHelper!!.updateData(dateFormat.format(date), Integer.toString(shortestSecondInt))
//                    Toast.makeText(context, "update data", Toast.LENGTH_SHORT).show()
//                } else {
//                    mDBHelper!!.addData(dateFormat.format(date), Integer.toString(shortestSecondInt))
//                    Toast.makeText(context, "add new data", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                shortestSecond = java.lang.String.format("本日最短秒數 : %ss\n", shortestSecondInt)
//                textViewShortestSecond!!.text = shortestSecond
//            }
//            countdowntimer!!.cancel()
//        }
//
//        // A Chord
//        if (pitchInHz >= 55.00 && pitchInHz < 58.27) {
//            textViewShowChord!!.text = "A"
//            val str = "A"
//            if (currentChord == str) {
//                a_chord_accu++
//                // 和弦彈奏累積到3次才觸發
//                if (a_chord_accu >= 3) {
//                    handler.sendEmptyMessage(TEXT_TO_SWITCH)
//                    //handler.sendEmptyMessage(START_COUNT_DOWN_TIMING);
//                    count++
//                }
//            }
//        } else if (pitchInHz >= 65.40 && pitchInHz < 69.30 || pitchInHz >= 132.00 && pitchInHz <= 134.00) {
//            textViewShowChord!!.text = "C"
//            val str = "C"
//            if (currentChord == str) {
//                c_chord_accu++
//                if (c_chord_accu >= 3) {
//                    handler.sendEmptyMessage(TEXT_TO_SWITCH)
//                    //handler.sendEmptyMessage(START_COUNT_DOWN_TIMING);
//                    count++
//                }
//            }
//        } else if (pitchInHz >= 73.42 && pitchInHz < 77.78) {
//            textViewShowChord!!.text = "D"
//            val str = "D"
//            if (currentChord == str) {
//                d_chord_accu++
//                if (d_chord_accu >= 3) {
//                    handler.sendEmptyMessage(TEXT_TO_SWITCH)
//                    //handler.sendEmptyMessage(START_COUNT_DOWN_TIMING);
//                    count++
//                }
//            }
//        } else if (pitchInHz >= 81.00 && pitchInHz <= 83.31 || pitchInHz >= 162.00 && pitchInHz >= 164.00) {
//            textViewShowChord!!.text = "E"
//            val str = "E"
//            if (currentChord == str) {
//                e_chord_accu++
//                if (e_chord_accu >= 2) {
//                    handler.sendEmptyMessage(TEXT_TO_SWITCH)
//                    //handler.sendEmptyMessage(START_COUNT_DOWN_TIMING);
//                    count++
//                }
//            }
//        } else if (pitchInHz >= 98.00 && pitchInHz < 103.83 || pitchInHz >= 48 && pitchInHz < 51.91) {
//            textViewShowChord!!.text = "G"
//            val str = "G"
//            if (currentChord == str) {
//                g_chord_accu++
//                if (g_chord_accu >= 3) {
//                    handler.sendEmptyMessage(TEXT_TO_SWITCH)
//                    //handler.sendEmptyMessage(START_COUNT_DOWN_TIMING);
//                    count++
//                }
//            }
//        }
//    }

    /**
     * API23以上除了Manifests要加permission
     * Runtime階段需添加callback函式
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AUDIO_PERMISSION_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Record Audio Permissoion Granted")
                // isGranted = true;

                // 初始Dispatcher;
                // initDispatcher();
            } else {
                Log.d(TAG, "Record Audio Permission Denied")
            }
        }
    }


    /**
     * 初始化Dispatcher
     * 內有兩個thread
     */
//    fun initDispatcher() {
//        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)
//        val pdh = PitchDetectionHandler { res, e ->
//            val pitchInHz = res.pitch
//
//            // here you check the value of getActivity() and break up if needed
//            if (activity == null) {
//                Log.v(TAG, "Activity is null")
//                return@PitchDetectionHandler
//            }
//
//
//            /* 1.runOnUiThread */
//            requireActivity().runOnUiThread { processPitch(pitchInHz) }
//        }
//        val pitchProcessor: AudioProcessor = PitchProcessor(
//                PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050F, 1024, pdh)
//        dispatcher?.addAudioProcessor(pitchProcessor)
//
//        /* 2.audioThread */
//        val audioThread = Thread(dispatcher, "Audio Thread")
//        audioThread.start()
//    }

    /**
     * 執行checkChord方法
     */
    fun initCheckChord() {
        requireActivity().runOnUiThread { checkChord() }
    }

    /**
     * 替換text
     */
    private fun next(scource: View?) {
        // 亂數顯示
        if (randomChordListIndex < randomChordList.size) {
            currentChord = randomChordList[randomChordListIndex]
            textSwitcher!!.setText(currentChord)
        }
        randomChordListIndex++

        /* 循序顯示
         if (curStr == chordList.length - 1) {
            curStr = 0;
            textSwitcher.setText(chordList[curStr]);
        }
        else {
            textSwitcher.setText(chordList[++curStr]);
        }
        */
    }

    /**
     * 產生不重複亂數序列
     */
    private fun createRandomNumberList(): IntArray {
        val list = IntArray(chordList.size)
        var i: Int
        var j: Int
        // 產生不重複亂數
        i = 0
        while (i < list.size) {
            list[i] = (Math.random() * chordList.size).toInt()
            j = 0
            while (j < i) {
                while (list[i] == list[j]) {
                    j = 0
                    list[i] = (Math.random() * chordList.size).toInt()
                }
                j++
            }
            i++
        }
        return list
    }


    /**
     * 啟動計時
     */
    private fun startTiming() {
        // CountDownTimer 是在UI thread執行
        countdowntimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(msUntilFinished: Long) {
                Log.v("TAG", "onTick → millisUntilFinished = " + msUntilFinished + ", seconds = " + Math.round(msUntilFinished.toDouble() / 1000))

                // 倒數
                //textViewShowSecond.setText(Integer.toString((int)(msUntilFinished/1000)+1));
                // 正數
                // 目前秒數

                totalSecondInt = (120000-msUntilFinished).toInt()/1000+1;
                textViewShowSecond!!.text = Integer.toString(totalSecondInt)

                // totalSecond = Integer.toString((int)totalSecondInt);

                totalSecond = java.lang.String.format("本次秒數 : %ss", totalSecondInt)
                progressBar!!.progress = totalSecondInt // (int)msUntilFinished
            }

            override fun onFinish() {
                progressBar!!.progress = 120
                totalSecond = java.lang.String.format("本次秒數 : %ss", totalSecondInt + 1)
                textViewTotalSecond!!.text = totalSecond
                handler.sendEmptyMessage(INVISIBLE_AND_VISIBLE_SOMETHING)
                //textViewShowSec.setText("Done!");
                //Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();
            }
        }.start()
    }

    /**
     * 查看count狀態
     */
    private fun checkCount(): Int {
        return count
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val TAG = "GameFragment"
        const val TEXT_TO_SWITCH = 1
        const val INVISIBLE_AND_VISIBLE_SOMETHING = 2
        const val START_COUNT_DOWN_TIMING = 3

        // 自定義授權碼
        private const val AUDIO_PERMISSION_CODE = 100

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GameFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): GameFragment {
            val fragment = GameFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }




}