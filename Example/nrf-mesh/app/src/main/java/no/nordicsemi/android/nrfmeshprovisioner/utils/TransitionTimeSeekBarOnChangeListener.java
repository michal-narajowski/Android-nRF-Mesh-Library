package no.nordicsemi.android.nrfmeshprovisioner.utils;

import android.widget.SeekBar;
import android.widget.TextView;

import no.nordicsemi.android.nrfmeshprovisioner.R;

public class TransitionTimeSeekBarOnChangeListener implements SeekBar.OnSeekBarChangeListener {
        private int lastValue = 0;
        private int resolution1 = 6;
        private int resolution2 = 6;
        private int resolution3 = 6;

        public int mTransitionStepResolution;
        public int mTransitionStep;
        private TextView time;

        public TransitionTimeSeekBarOnChangeListener(TextView time) {
            this.time = time;
        }

        @Override
        public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {

            if(progress >= 0 && progress <= 62) {
                resolution1 = 6;
                resolution2 = 6;
                resolution3 = 6;
                lastValue = progress;
                mTransitionStepResolution = 0;
                mTransitionStep = progress;
                double res = progress / 10.0;
                time.setText(String.format("%s %s", String.valueOf(res), "s"));
            } else if(progress >= 63 && progress <= 118) {
                resolution2 = 6;
                resolution3 = 6;
                if(progress > lastValue) {
                    resolution1 = progress - 56;
                    lastValue = progress;
                } else if (progress < lastValue){
                    resolution1 = -(56 - progress);
                }
                mTransitionStepResolution = 1;
                mTransitionStep = resolution3;
                time.setText(String.format("%s %s", String.valueOf(resolution1), "s"));

            } else if(progress >= 119 && progress <= 174) {
                resolution3 = 6;
                if(progress > lastValue) {
                    resolution2 = progress - 112;
                    lastValue = progress;
                } else if (progress < lastValue){
                    resolution2 = -(112 - progress);
                }
                mTransitionStepResolution = 2;
                mTransitionStep = resolution2;
                time.setText(String.format("%s %s", String.valueOf(resolution2 * 10), "s"));
            } else if(progress >= 175 && progress <= 230){
                if(progress >= lastValue) {
                    resolution3 = progress - 168;
                    lastValue = progress;
                } else if (progress < lastValue){
                    resolution3 = -(168 - progress);
                }
                mTransitionStepResolution = 3;
                mTransitionStep = resolution3;
                time.setText(String.format("%s %s", String.valueOf(resolution3 * 10), "min"));
            }
        }

        @Override
        public void onStartTrackingTouch(final SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(final SeekBar seekBar) {

        }
}
