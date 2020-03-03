package as;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Computes the Mandelbrot set.
 * http://en.wikipedia.org/wiki/Mandelbrot_set
 */
public class Mandelbrot {
    public static final int IMAGE_LENGTH = 1024;
    public static final int MAX_ITERATIONS = 512;

    public static final int COLOR_COUNT = 64;
    private static Color[] colors = generateColors(COLOR_COUNT);

    private static Color getColor(int iterations) {
        return iterations == MAX_ITERATIONS ? Color.BLACK : colors[iterations % COLOR_COUNT];
    }

    private static Color[] generateColors(int n) {
        Color[] cols = new Color[n];
        for (int i = 0; i < n; i++) {
            cols[i] = Color.hsb(((float) i / (float) n) * 360, 0.85f, 1.0f);
        }
        return cols;
    }

    public static void computeSequential(PixelPainter painter, Plane plane, CancelSupport cancel) {
        double half = plane.length / 2;
        double reMin = plane.center.r - half;
        double imMax = plane.center.i + half;
        double step = plane.length / IMAGE_LENGTH;

        for (int x = 0; x < IMAGE_LENGTH && !cancel.isCancelled(); x++) { // x-axis
            double re = reMin + x * step; // map pixel to complex plane
            for (int y = 0; y < IMAGE_LENGTH; y++) { // y-axis
                double im = imMax - y * step; // map pixel to complex plane

                //int iterations = mandel(re, im);
                int iterations = mandel(new Complex(re, im));
                painter.paint(x, y, getColor(iterations));
            }
        }
    }

    public static void computeParallel(PixelPainter painter, Plane plane, CancelSupport cancel) {
        final int N = Runtime.getRuntime().availableProcessors();
        final int heightPerThread = IMAGE_LENGTH / N;
        final List<Thread> threads = new ArrayList<Thread>(N);
        // Create N slices
        for (int i = 0; i < N; i++) {
            final int startX = i * heightPerThread;
            final int endX = (i < N - 1) ? startX + heightPerThread : IMAGE_LENGTH;
            Thread thread = new Thread(new xMandel( painter, plane, cancel,startX, endX));
            threads.add(thread);
            thread.start(); // Start all Threads
        }
        for (Thread thread : threads) {
            try {
                thread.join(); // Wait for all Threads
            } catch (InterruptedException e) {
                /* Ignored */
            }
        }
    }

    static class xMandel implements Runnable{
        private final int startX, endX; private final Plane plane;
        private final PixelPainter painter; private CancelSupport cancel;
        private xMandel(PixelPainter pp, Plane p, CancelSupport cs, int startX, int endX) {
            this.startX = startX; this.endX = endX; this.painter = pp; this.plane = p; this.cancel = cs;
        }
        @Override public void run() {
            double half = plane.length / 2; double reMin = plane.center.r - half;
            double imMax = plane.center.i + half; double step = plane.length / IMAGE_LENGTH;
            for (int x = startX; x < endX && !cancel.isCancelled(); x++) {
                double re = reMin + x * step;
                for (int y = 0; y < IMAGE_LENGTH; y++) {
                    double im = imMax - y * step;
                    int iterations = mandel(re, im);
                    painter.paint(x, y, getColor(iterations));
                }
            }
        }
        /*
        private int start, slice;
        private CancelSupport cancel;
        private Plane plane;
        private double reMin, imMax,step;
        private PixelPainter painter;
        public xMandel(PixelPainter painter, Plane plane, CancelSupport cancel, int start, int slice){
            this.start = start;
            this.slice = slice;
            this.cancel = cancel;
            this.plane = plane;

            double half = plane.length / 2;
            double reMin = plane.center.r - half;
            double imMax = plane.center.i + half;
            double step = plane.length / IMAGE_LENGTH;
        }
        @Override
        public void run() {
            for (int x = start; x < slice && !cancel.isCancelled(); x++) { // x-axis
                double re = reMin + x * step; // map pixel to complex plane
                for (int y = 0; y < IMAGE_LENGTH; y++) { // y-axis
                    double im = imMax - y * step; // map pixel to complex plane

                    //int iterations = mandel(re, im);
                    int iterations = mandel(new Complex(re, im));
                    painter.paint(x, y, getColor(iterations));
                }
            }
        }*/
    }

    /**
     * z_n+1 = z_n^2 + c starting with z_0 = 0
     * <p>
     * Checks whether c = re + i*im is a member of the Mandelbrot set.
     *
     * @return the number of iterations
     */
    public static int mandel(Complex c) {
        Complex z = Complex.ZERO;
        int iterations = 0;
        while (z.absSq() <= 4 && iterations < MAX_ITERATIONS) {
            z = z.pow(2).plus(c);
            iterations++;
        }
        return iterations;
    }

    /**
     * Same as {@code Mandelbrot#mandel(Complex)} but more efficient.
     */
    public static final int mandel(double cre, double cim) {
        double re = 0.0;
        double im = 0.0;
        int iterations = 0;
        while (re * re + im * im <= 4 && iterations < MAX_ITERATIONS) {
            double re1 = re * re - im * im + cre;
            double im1 = 2 * re * im + cim;
            re = re1;
            im = im1;
            iterations++;
        }
        return iterations;
    }
}
