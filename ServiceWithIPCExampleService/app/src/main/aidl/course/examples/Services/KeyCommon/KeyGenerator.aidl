package course.examples.Services.KeyCommon;

interface KeyGenerator {

    Bitmap getPicture(int id);
    void play(int id);
    void pause(int id);
    void resume(int id);
    void stop(int id);
}