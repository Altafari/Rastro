package rastro.model;

public class ScanningShape {
    private boolean[][] shape;
    
    public ScanningShape(float width, float height) {
        float radX = Math.round(width);
        float radY = Math.round(height);
        int szX = 2 * (int)radX + 1;
        int szY = 2 * (int)radY + 1;
        radX = radX + 0.2f;
        radY = radY + 0.2f;
        shape = new boolean[szY][szX];
        for (int j = 0; j < szX; j++) {
            float x = (float)(j - szX / 2);
            for(int i = 0; i < szY; i++) {
                float y = (float)(i - szY / 2);
                float rvect2 = (x * x) / (radX * radX) + (y * y) / (radY * radY);
                shape[i][j] = rvect2 <= 1.0f; 
            }
        }
    }
    public boolean[][] getShape() {
        return shape;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (boolean[] row : shape) {
            for (boolean b : row) {
                sb.append(b? "1 " : "0 ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
