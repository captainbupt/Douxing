package org.vudroid.pdfdroid.codec;

import org.vudroid.core.codec.CodecDocument;
import org.vudroid.core.codec.CodecPage;


public class PdfDocument implements CodecDocument
{
    private long docHandle;
    private static final int FITZMEMORY = 512 * 1024;

    private PdfDocument(long docHandle)
    {
        this.docHandle = docHandle;
    }

    @Override
	public CodecPage getPage(int pageNumber)
    {
        return PdfPage.createPage(docHandle, pageNumber + 1);
    }

    @Override
	public int getPageCount()
    {
        return getPageCount(docHandle);
    }

    static PdfDocument openDocument(String fname, String pwd)
    {
        try {
        	PdfDocument pdfDocument = new PdfDocument(open(FITZMEMORY, fname, pwd));
        	return pdfDocument;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

    private static native long open(int fitzmemory, String fname, String pwd);

    private static native void free(long handle);

    private static native int getPageCount(long handle);

    @Override
    protected void finalize() throws Throwable
    {
        recycle();
        super.finalize();
    }

    @Override
	public synchronized void recycle() {
        if (docHandle != 0) {
            free(docHandle);
            docHandle = 0;
        }
    }
}
