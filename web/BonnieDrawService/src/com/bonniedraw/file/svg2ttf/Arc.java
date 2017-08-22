package com.bonniedraw.file.svg2ttf;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an SVG arc. It contains methods to convert an Arc to
 * Bezier curves. Caveat: tested only with radiusX=radiusY.
 * 
 * This class is part of Svg2Ttf, an SVG to TTF Font Converter. It is based on
 * DoubleType, a graphical typeface designer. Hence, this code is made available
 * under a GNU General Public License as published by the Free Software
 * Foundation.
 * 
 * @author Fabian M. Suchanek
 * 
 */
public class Arc {
    /** Center X */
    public double cx;
    /** Center Y */
    public double cy;
    /** X radius */
    public double rx;
    /** Y radius */
    public double ry;
    /** Start angle in radians, from the left, going counterclockwise */
    public double theta;
    /** Size of the arc in radians, going counterclockwise */
    public double delta;

    /**
     * Computes an arc from an SVG arc command, as per
     * http://www.w3.org/TR/SVG/implnote.html#ArcImplementationNotes
     */
    public Arc(double x1, double y1, double x2, double y2, double rx, double ry, double sweepflag, double largeflag) {
        this.rx = rx;
        this.ry = ry;
        double x1p = (x1 - x2) / 2;
        double y1p = (y1 - y2) / 2;
        double sqrt = Math.sqrt(Math.abs((rx * rx * ry * ry - rx * rx * y1p * y1p - ry * ry * x1p * x1p)
                / (rx * rx * y1p * y1p + ry * ry * x1p * x1p)));
        if (largeflag == sweepflag) sqrt = -sqrt;
        double cxp = sqrt * rx * y1p / ry;
        cx = cxp + (x1 + x2) / 2;
        double cyp = -sqrt * ry * x1p / rx;
        cy = cyp + (y1 + y2) / 2;
        theta = Arc.angle(1, 0, (x1p - cxp) / rx, (y1p - cyp) / ry);
        delta = Arc.angle((x1p - cxp) / rx, (y1p - cyp) / ry, (-x1p - cxp) / rx, (-y1p - cyp) / ry);
        if (sweepflag == 0 && delta > 0) delta -= Math.PI * 2;
        else if (sweepflag == 1 && delta < 0) delta += Math.PI * 2;
    }

    /** TRUE if all values are non NAN numbers */
    public boolean isOK() {
        return (!Double.isNaN(delta) && !Double.isNaN(theta) && delta != 0);
    }

    /**
     * Converts the arc to a series of Bezier points (x, y, on/off); without the
     * first point
     */
    public List<double[]> asBezier() {
        final double quarter = Math.PI / 4;
        List<double[]> result = new ArrayList<>();
        double lastAngle = 0; // relative to theta
        double newAngle = 0;
        do {

            if (delta > 0) {
                newAngle += quarter;
                if (newAngle > delta) newAngle = delta;
            } else {
                newAngle -= quarter;
                if (newAngle < delta) newAngle = delta;
            }
            // Off-curve point
            double midAngle = (newAngle + lastAngle) / 2;
            double radius = Math.sqrt(rx * rx + Math.tan(midAngle - lastAngle) * rx * Math.tan(midAngle - lastAngle)
                    * rx);
            result.add(new double[] { Arc.toX(cx, cy, theta + midAngle, radius),
                    Arc.toY(cx, cy, theta + midAngle, radius), 0.0 });
            // Final on-curve point
            result.add(new double[] { Arc.toX(cx, cy, theta + newAngle, rx), Arc.toY(cx, cy, theta + newAngle, ry), 1.0 });
            lastAngle = newAngle;
        } while (newAngle != delta);
        return (result);
    }

    @Override
    public String toString() {
        return "Arc center=" + cx + ", " + cy + "; radius: " + rx + ", " + ry + " start: " + (theta / Math.PI * 180)
                + " angle: " + (delta / Math.PI * 180);
    }

    /** returns y-coordinate for angular coordinates */
    public static double toY(double cx, double cy, double angle, double r) {
        return cy + r * Math.sin(angle);
    }

    /** returns x-coordinate for angular coordinates */
    public static double toX(double cx, double cy, double angle, double r) {
        return cx + r * Math.cos(angle);
    }

    /** returns the angle between two points */
    public static double angle(double ux, double uy, double vx, double vy) {
        double val = Math.acos(scalprod(ux, uy, vx, vy) / norm(ux, uy) / norm(vx, vy));
        return (ux * vy - uy * vx < 0 ? -val : val);
    }

    /** returns norm of a vector */
    public static double norm(double ux, double uy) {
        return (Math.sqrt(scalprod(ux, uy, ux, uy)));
    }

    /** returns the scalar product of two points */
    public static double scalprod(double ux, double uy, double vx, double vy) {
        return (ux * vx + uy * vy);
    }
}