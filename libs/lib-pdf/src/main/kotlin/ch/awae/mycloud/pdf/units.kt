package ch.awae.mycloud.pdf

private const val MM_PER_INCH = 25.4f
private const val POINTS_PER_INCH = 72f

/**
 * PDF uses points as its base unit.
 * This constant converts millimeters to PDF points.
 */
const val POINTS_PER_MM: Float = POINTS_PER_INCH / MM_PER_INCH