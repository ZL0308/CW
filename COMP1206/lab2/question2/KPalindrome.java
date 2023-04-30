import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.recursion.PalindromeChecker;

public class KPalindrome implements PalindromeChecker {

  @Override
  public boolean isKPalindrome(String s, int i) {

    int l = s.length();
    if (i < 0) return false;
    if (s.charAt(0) == s.charAt(l - 1)) {
      if (l > 3) return isKPalindrome(s.substring(1, l - 1), i);
    } else {
      return isKPalindrome(s.substring(0, l - 1), i-1) || isKPalindrome(s.substring(1, l), i-1);
    }
    return true;
  }
}

