package java_course;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

public class chess_board
{
    // Initialize the size of the board and button array
    static final int BOARD_SIZE = 8;
    static JButton[][] buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
    static String[][] board = new String[BOARD_SIZE][BOARD_SIZE];  // to store piece names
    static boolean whiteTurn = true;  // white starts
    static int selectedX = -1, selectedY = -1;  // Store the selected piece's position
    static boolean enPassantPossible = false;
    static int enPassantX = -1, enPassantY = -1;

    public static void main(String args[]) {
        JFrame frame = new JFrame("Chess Board");
        frame.setSize(600, 600);
        frame.setLayout(new GridLayout(8, 8));

        // Initialize buttons and board
        initializeBoard(frame);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void initializeBoard(JFrame frame) {
        // Initialize piece images
        ImageIcon blackrook = new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\black rook.png");
        ImageIcon horse = new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\horse.png");
        ImageIcon bishop = new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\bishop.jpeg");
        ImageIcon queen = new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\queen.jpg");
        ImageIcon king = new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\king.jpg");
        ImageIcon soldier = new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\soldier.jpeg");

        ImageIcon white_rook = new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\white_rook.jpg");
        ImageIcon white_horse = new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\white_horse.png");
        ImageIcon white_bishop = new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\white_bishop.jpeg");
        ImageIcon white_queen = new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\white_queen.jpg");
        ImageIcon white_king = new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\white_king.jpg");
        ImageIcon white_soldier = new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\white_soldier.jpg");

        // Initialize the board and set up the pieces
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.BLACK);
                frame.add(buttons[i][j]);

                // Set up initial piece arrangement
                if (i == 0) {
                    // Black pieces
                    board[i][j] = getBlackPiece(j);
                } else if (i == 1) {
                    board[i][j] = "black_pawn";
                } else if (i == 6) {
                    board[i][j] = "white_pawn";
                } else if (i == 7) {
                    // White pieces
                    board[i][j] = getWhitePiece(j);
                } else {
                    board[i][j] = null; // Empty squares
                }

                // Set the button icons based on the board setup
                updateButtonIcon(i, j);

                // Add action listener for each button
                final int x = i, y = j;
                buttons[i][j].addActionListener(e -> onButtonClick(x, y));
            }
        }
    }

    public static void onButtonClick(int x, int y) {
        if (selectedX == -1 && selectedY == -1 && board[x][y] != null && 
            ((whiteTurn && board[x][y].startsWith("white")) || 
            (!whiteTurn && board[x][y].startsWith("black")))) {
            // Select a piece
            selectedX = x;
            selectedY = y;
            System.out.println("Selected " + board[x][y] + " at " + x + "," + y);
        } else if (selectedX != -1 && selectedY != -1) {
            // Move the piece if a valid target
            movePiece(selectedX, selectedY, x, y);
            // Reset selection
            selectedX = -1;
            selectedY = -1;
        }
    }

    public static void movePiece(int fromX, int fromY, int toX, int toY) {
        // Validate the move (simplified for this example)
        if (isValidMove(fromX, fromY, toX, toY)) {
            // Check for En Passant capture
            if (enPassantPossible && toX == enPassantX && toY == enPassantY) {
                // Capture the pawn as part of En Passant
                board[fromX][fromY] = null;
                board[toX][toY] = board[fromX][fromY];  // Move the pawn
                board[enPassantX][enPassantY] = null;  // Remove the captured pawn
                updateButtonIcon(fromX, fromY);
                updateButtonIcon(toX, toY);
                enPassantPossible = false;  // Reset En Passant possibility
            } else {
                // Regular move
                board[toX][toY] = board[fromX][fromY];
                board[fromX][fromY] = null;
                updateButtonIcon(fromX, fromY);
                updateButtonIcon(toX, toY);
            }

            // Handle pawn promotion if it reaches the last rank
            if ((board[toX][toY].startsWith("white") && toX == 0) || 
                (board[toX][toY].startsWith("black") && toX == 7)) {
                promotePawn(toX, toY);
            }

            // Change turn
            whiteTurn = !whiteTurn;
            System.out.println((whiteTurn ? "White's turn" : "Black's turn"));
        } else {
            System.out.println("Invalid move!");
        }
    }

    public static boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        // For now, we assume any move within bounds is valid.
        String movingPiece = board[fromX][fromY];
        if (movingPiece == null) {
            return false;  // No piece to move
        }

        if (movingPiece.startsWith("white") || movingPiece.startsWith("black")) {
            if (movingPiece.endsWith("rook")) {
                return isValidRookMove(fromX, fromY, toX, toY);
            }
            if (movingPiece.endsWith("pawn")) {
                return isValidPawnMove(fromX, fromY, toX, toY, movingPiece);
            }
        }

        return toX >= 0 && toX < BOARD_SIZE && toY >= 0 && toY < BOARD_SIZE;
    }

    public static boolean isValidRookMove(int fromX, int fromY, int toX, int toY) {
        // Check if the move is along the same row or column
        if (fromX != toX && fromY != toY) {
            return false;  // Rooks can only move in straight lines (rows or columns)
        }

        // Check if there are no pieces between the from and to positions
        if (fromX == toX) {
            // Same row, check if the path is clear
            int step = (toY > fromY) ? 1 : -1;
            for (int y = fromY + step; y != toY; y += step) {
                if (board[fromX][y] != null) {
                    return false;  // Path is blocked by another piece
                }
            }
        } else if (fromY == toY) {
            // Same column, check if the path is clear
            int step = (toX > fromX) ? 1 : -1;
            for (int x = fromX + step; x != toX; x += step) {
                if (board[x][fromY] != null) {
                    return false;  // Path is blocked by another piece
                }
            }
        }

        return true;
    }

    public static boolean isValidPawnMove(int fromX, int fromY, int toX, int toY, String piece) {
        int direction = piece.startsWith("white") ? -1 : 1;  // White moves up, black moves down
        int startingRow = piece.startsWith("white") ? 6 : 1;  // Starting row for white is 6, for black is 1

        // Regular one-square move forward
        if (fromY == toY && toX == fromX + direction && board[toX][toY] == null) {
            return true;
        }

        // Pawn's first move (two squares forward)
        if (fromY == toY && toX == fromX + 2 * direction && board[toX][toY] == null && 
            (fromX == startingRow)) {
            enPassantPossible = false; // Reset en passant on normal moves
            return true;
        }

        // Capturing diagonally
        if (Math.abs(fromY - toY) == 1 && toX == fromX + direction && board[toX][toY] != null) {
            return true;
        }

        // En Passant (special rule)
        if (Math.abs(fromY - toY) == 1 && toX == fromX + direction && 
            board[toX][toY] == null && 
            (board[fromX][fromY].endsWith("pawn") && 
             board[toX][fromY] != null && board[toX][fromY].startsWith("black") == !whiteTurn)) {
            enPassantX = toX;
            enPassantY = toY;
            enPassantPossible = true;
            return true;
        }

        return false;
    }

    public static void updateButtonIcon(int x, int y) {
        // Clear current icon
        buttons[x][y].setIcon(null);

        // Set the new icon for the piece, if any
        if (board[x][y] != null) {
            ImageIcon pieceIcon = getPieceIcon(board[x][y]);
            if (pieceIcon != null) {
                Image image = pieceIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                buttons[x][y].setIcon(new ImageIcon(image));
            }
        }
    }

    public static ImageIcon getPieceIcon(String piece) {
        // Return the corresponding image icon for each piece
        switch (piece) {
            case "white_rook": return new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\white_rook.jpg");
            case "black_rook": return new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\black rook.png");
            case "white_horse": return new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\white_horse.png");
            case "black_horse": return new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\horse.png");
            case "white_bishop": return new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\white_bishop.jpeg");
            case "black_bishop": return new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\bishop.jpeg");
            case "white_queen": return new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\white_queen.jpg");
            case "black_queen": return new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\queen.jpg");
            case "white_king": return new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\white_king.jpg");
            case "black_king": return new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\king.jpg");
            case "white_pawn": return new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\white_soldier.jpg");
            case "black_pawn": return new ImageIcon("C:\\Users\\gayat\\OneDrive\\Desktop\\chess board images\\soldier.jpeg");
            default: return null;
        }
    }

    public static String getBlackPiece(int column) {
        // Return the piece based on the column for black pieces
        switch (column) {
            case 0: case 7: return "black_rook";
            case 1: case 6: return "black_horse";
            case 2: case 5: return "black_bishop";
            case 3: return "black_queen";
            case 4: return "black_king";
            default: return null;
        }
    }

    public static String getWhitePiece(int column) {
        // Return the piece based on the column for white pieces
        switch (column) {
            case 0: case 7: return "white_rook";
            case 1: case 6: return "white_horse";
            case 2: case 5: return "white_bishop";
            case 3: return "white_queen";
            case 4: return "white_king";
            default: return null;
        }
    }

    // Promote pawn to Queen (for simplicity)
    public static void promotePawn(int x, int y) {
        board[x][y] = (board[x][y].startsWith("white") ? "white_queen" : "black_queen");
        updateButtonIcon(x, y);
    }
}
