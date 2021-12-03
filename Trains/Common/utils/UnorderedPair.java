package utils;

import java.util.Objects;

// TODO: Consider having a pair class that holds two elements, and two subclasses: ordered and unordered
// that define equality, etc.
/**
 * Represents a collection of 2 non-null elements for which order does not matter in equality
 * checking.
 *
 * <p>If the individual elements are immutable, then the utils.UnorderedPair is also immutable.
 *
 * @param <T> the type of the elements.
 */
public class UnorderedPair<T> {

  /** Gets the "left" element. */
  public final T left;

  /** Gets the "right" element. */
  public final T right;

  /**
   * Constructs this from the two given elements, parameter names correspond to fields.
   *
   * @param left one of the elements.
   * @param right another element.
   * @throws NullPointerException if either element is null.
   */
  public UnorderedPair(T left, T right) {
    this.left = Objects.requireNonNull(left);
    this.right = Objects.requireNonNull(right);
  }

  /**
   * Constructs this from the given unordered pair, preserving parameter name and references to
   * original elements..
   *
   * @param toClone the unordered pair to clone.
   * @throws NullPointerException if either element is null.
   */
  public UnorderedPair(UnorderedPair<T> toClone) {
    this(Objects.requireNonNull(toClone).left, Objects.requireNonNull(toClone).right);
  }

  /**
   * Computes the hashcode for this unordered pair.
   *
   * @return the sum of the hashcodes of the individual elements.
   */
  @Override
  public int hashCode() {
    return left.hashCode() + right.hashCode();
  }

  /**
   * Determines whether this unordered pair is equal to another object, relying on equality for the
   * type of element in this pair.
   *
   * @param other the other object to compare to.
   * @return a boolean indicating whether the other object is an utils.UnorderedPair that contains the
   *     same elements as this.
   */
  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }

    if (!(other instanceof UnorderedPair<?>)) {
      return false;
    }

    UnorderedPair<?> otherUnorderedPair = (UnorderedPair<?>) other;

    return orderedEquals(otherUnorderedPair.left, otherUnorderedPair.right)
        || orderedEquals(otherUnorderedPair.right, otherUnorderedPair.left);
  }

  @Override
  public String toString() {
    return "{" + this.left.toString() + ", " + this.right.toString() + "}";
  }

  /**
   * Determines whether the given elements are equal to this' elements in a particular order.
   *
   * @param otherElement1 element to compare to this.left.
   * @param otherElement2 element to compare to this.right.
   * @return whether both elements math this' elements in the particular order.
   */
  private boolean orderedEquals(Object otherElement1, Object otherElement2) {
    return this.left.equals(otherElement1) && this.right.equals(otherElement2);
  }
}
