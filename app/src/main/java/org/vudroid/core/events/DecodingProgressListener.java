package org.vudroid.core.events;

public interface DecodingProgressListener
{
    void decodingProgressChanged(int currentlyDecoding);

    class DecodingProgressEvent extends SafeEvent<DecodingProgressListener>
    {
        private final int currentlyDecoding;

        public DecodingProgressEvent(int currentlyDecoding)
        {
            this.currentlyDecoding = currentlyDecoding;
        }

        @Override
        public void dispatchSafely(DecodingProgressListener listener)
        {
            listener.decodingProgressChanged(currentlyDecoding);
        }
    }
}
