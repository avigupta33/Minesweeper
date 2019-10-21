class MineSweeper
{
  public static void main(String[] args) 
  {
    MineModel mineModel = new AvisModel();
    
    MineView mineView = new MineView(mineModel, 600, 400);
  }
}